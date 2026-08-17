# Especificação Arquitetural: Sistema de Agendamento e Calibração

## 1. Visão Geral
Este documento define a arquitetura, o fluxo de dados e os requisitos para o novo sistema backend de automatização e agendamento de calibrações de instrumentos industriais. O sistema substituirá o atual controle manual via planilhas, orquestrando as manutenções juntamente a empresas terceirizadas e conectando os resultados finais com o ERP corporativo principal da indústria.

O backend atuará como o hub integrador e a fonte da verdade da rastreabilidade técnica dos instrumentos, projetado para alta escalabilidade suportando o modelo multi-filial (Multi-Tenancy).

## 2. Abordagem Arquitetural
Foi escolhida a arquitetura de **Microserviços Distribuídos Orientados a Eventos**, com o **Apache Kafka** atuando como espinha dorsal de mensageria assíncrona. 

### Microserviços Principais
1. **Identity & Access Management (IAM Service):** Serviço central de autenticação e autorização (RBAC) responsável por emitir e revalidar tokens JWT, além do gerenciamento de usuários e perfis.
2. **Equipment Service (Core):** Domínio de parâmetros globais, instrumentos, equipamentos e estrutura organizacional das filiais.
3. **Scheduling Service (Agendamentos):** Motor de regras de negócio de vencimentos, jobs de verificação diária e controle do aceite do instrumentista.
4. **Integration Service (Ponte):** Gateway de comunicação com o ecossistema externo (API da Terceirizada e o ERP Corporativo).

## 3. Modelagem Relacional (MER) - Estrutura Lógica Base
A persistência garante isolamento lógico por fábrica aplicando o UUID `id_filial` à maior parte das tabelas de domínio.

**1. `filial` (Tenant)**
- `id_filial` (PK, UUID)
- `nome`, `cnpj`, `endereco`

**2. `usuario` (IAM)**
- `id_usuario` (PK, UUID)
- `id_filial` (FK - podendo ser '-1' ou Nulo para o admin global do sistema)
- `login`, `senha_hash` (Armazenado via Bcrypt)
- `role` (Enum: ADM, INSTRUMENTISTA)
- *Nota:* O sistema deverá inicializar automaticamente com um usuário padrão (`login: admin`, `senha: admin` - com hash aplicado).

**3. `parametro_metadado` (Dicionário de Parâmetros)**
- `id_parametro` (PK, String/UUID)
- `descricao` (String)
- `por_filial` (Boolean)
- `tipo_valor` (Enum: NUMERO, LISTA, BOOLEAN, TEXTO)

**4. `parametro_valor` (Valores Aplicados)**
- `id_valor` (PK, UUID)
- `id_parametro` (FK)
- `id_filial` (Identificador da filial ou '-1' caso o parâmetro seja global, i.e., `por_filial = false`)
- `valor_parametro` (String/Texto comportando o valor convertido)

**5. `parametro_lista_valores` (Opções para parâmetros do tipo LISTA)**
- `id_lista_valor` (PK, UUID)
- `id_parametro` (FK)
- `descricao` (Ex: "Ativo", "Inativo", "Prioridade Alta")
- `valor_armazenado` (Ex: "1", "0", "HIGH")

**6. `empresa_terceirizada`**
- `id_terceirizada` (PK, UUID)
- `cnpj`, `nome`

**7. `instrumento`**
- `id_instrumento` (PK, UUID)
- `id_filial` (FK - Isolamento por fábrica)
- `tag`, `cor_etiqueta`, `nome`, `area`, `localizacao`
- `id_equipamento_pai` (FK para relacionamento hierárquico)
- `descricao`, `range`, `faixa_uso`
- `criticidade` (Enum: PCC / PPRO)
- `equipamento_critico` (Boolean)
- `unidade_medida`, `numero_sap`, `numero_plano`
- `agendamento_automatico` (Boolean - Parametriza o fluxo de gatilho)

**8. `agendamento_calibracao`**
- `id_agendamento` (PK, UUID)
- `id_instrumento` (FK)
- `id_terceirizada` (FK)
- `data_calibracao_atual`, `data_proxima_calibracao`
- `status` (Enum: PENDENTE_AVISO, AGUARDANDO_APROVACAO, AGENDADO_NA_TERCEIRIZADA, CONCLUIDO, VENCIDO)

**9. `certificado`**
- `id_certificado` (PK, UUID)
- `id_agendamento` (FK)
- `numero_certificado_anterior`, `numero_certificado_atual`
- `erro_maximo_encontrado`, `ponto_maior_erro`, `erro_atual`, `erro_maximo_aceitavel`
- `houve_correcao` (Boolean)
- `responsavel_validacao`
- `data_recebimento`

## 4. Fluxo de Funcionamento (Workflow Desacoplado via Kafka)

- **Fase 1: Cadastros Iniciais (Setup)**
  O usuário cadastra a Filial, Terceirizada e os Instrumentos no `Equipment Service`. Este publica um evento `instrument-created-topic` no Kafka, consumido pelo `Scheduling Service` para iniciar a linha do tempo do equipamento.
- **Fase 2: Núcleo de Schedule (Job e Aprovação)**
  O `Scheduling Service` varre os instrumentos a vencer através de rotinas em cron. Ao identificar proximidade, ele emite o evento `calibration-approaching-topic` (Notificação) e muda o status. Se o instrumento estiver parametrizado com `agendamento_automatico=TRUE`, avança sozinho; caso contrário, o instrumentista visualiza a tela e o aprova manualmente, gerando o evento `calibration-scheduling-approved-topic`.
- **Fase 3: Integrações (A Marcação e o Certificado)**
  O `Integration Service` escuta o agendamento aprovado, executa um POST contra a API da Terceirizada e registra o protocolo. Ao final da execução física da calibração, a API externa envia um Webhook contendo os dados do Certificado, disparando internamente o evento `calibration-certificate-received-topic`. O `Scheduling Service` ouve este evento e calcula as novas datas de vencimento, encerrando o ciclo.
- **Fase 4: Exportação (Fechamento)**
  Garantindo o repasse de informações, o `Integration Service` orquestra a submissão dos dados finais compilados (N° SAP, Novo Certificado, Erros) para os endpoints do ERP, finalizando a integração de dados da organização.

## 5. Requisitos do Sistema

### Requisitos Funcionais (RF)
1. **Gestão de Cadastro (Multi-Filial):** CRUD completo dos ativos mantendo o Tenant ID (Filial).
2. **Gestão de Usuários e Acessos (IAM):** Autenticação e Autorização de WebAPIs baseada em tokens JWT (com renovação a cada 1h via refresh token). Cadastro de usuários com perfis segregados (ADM vs INSTRUMENTISTA). Geração automática de usuário padrão admin e armazenamento de senhas com algoritmo Bcrypt.
3. **Parâmetros Dinâmicos (Dicionário de Dados):** Sistema flexível de configuração de negócio, permitindo criar metadados (tipos numéricos, booleanos, textos ou listas) com atribuições globais (para o sistema todo) ou locais (por filial).
4. **Controle de Automação:** Flag no cadastro do instrumento definindo agendamento manual ou fluxo automático (Machine-to-Machine).
5. **Monitoramento Contínuo (Job):** Rotina Background para verificar janelas de vencimento baseadas na data da próxima calibração.
6. **Notificações:** Disparo de alertas aos usuários da respectiva filial sempre que um agendamento entrar no status PENDENTE_AVISO.
7. **Aprovação Manual:** Interface ou endpoint permitindo o "Aceite" do instrumentista em workflows não automatizados.
8. **Recepção de Certificados:** Endpoint (Webhook) dedicado a receber certificados, erro máximo, erro atual e tolerâncias fornecidos pela provedora de calibração.
9. **Integração ERP:** Envio sistemático do pacote de dados da calibração concluída para o ERP central.
10. **Histórico:** Retorno do tracking e log linear de todas as calibrações passadas de um determinado TAG de instrumento.

### Requisitos Não Funcionais (RNF)
1. **Arquitetura Assíncrona:** Separação de fronteiras com DDD, construída sob Spring Boot e mensageria distribuída com Apache Kafka.
2. **Multi-Tenancy:** Forte isolamento por Tenant de forma a prover integridade e segurança de dados caso mais unidades fabris adotem o mesmo ambiente.
3. **Resiliência Externa:** Padrões Circuit Breaker e Retries aplicados às chamadas saíntes para o ERP e Terceirizadas, absorvendo instabilidades das redes (Resilience4j).
4. **Outbox Pattern:** Uso do padrão Transactional Outbox em cima das bases relacionais para garantir a entrega Atomic-Event no Kafka e prevenir vazamentos de dados por falha de broker.
5. **Observabilidade Total:** Distributed Tracing obrigatório, integrando Trace/Span IDs end-to-end, facilitando depurações e leitura centralizada por APMs (ex: Zipkin/Jaeger).
6. **Segurança de APIs (JWT):** Todos os endpoints expostos devem exigir Bearer Token. A estratégia de expiração deve invalidar tokens JWT estáticos a cada 1 hora, exigindo um fluxo de Refresh Token gerido pelo IAM para manter o acesso do usuário logado de forma segura.
