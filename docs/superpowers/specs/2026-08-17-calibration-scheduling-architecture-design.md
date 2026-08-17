# Especificação Arquitetural: Sistema de Agendamento e Calibração

## 1. Visão Geral
Este documento define a arquitetura, o fluxo de dados e os requisitos para o novo sistema backend de automatização e agendamento de calibrações de instrumentos industriais. O sistema substituirá o atual controle manual via planilhas, orquestrando as manutenções juntamente a empresas terceirizadas e conectando os resultados finais com o ERP corporativo principal da indústria.

O backend atuará como o hub integrador e a fonte da verdade da rastreabilidade técnica dos instrumentos, projetado para alta escalabilidade suportando o modelo multi-filial (Multi-Tenancy).

## 2. Abordagem Arquitetural
Foi escolhida a arquitetura de **Microserviços Distribuídos Orientados a Eventos**, com o **Apache Kafka** atuando como espinha dorsal de mensageria assíncrona. 

### Microserviços Principais
1. **Equipment Service (Core):** Domínio de instrumentos, equipamentos e estrutura organizacional das filiais.
2. **Scheduling Service (Agendamentos):** Motor de regras de negócio de vencimentos, jobs de verificação diária e controle do aceite do instrumentista.
3. **Integration Service (Ponte):** Gateway de comunicação com o ecossistema externo (API da Terceirizada e o ERP Corporativo).

## 3. Modelagem Relacional (MER) - Estrutura Lógica Base
A persistência garante isolamento lógico por fábrica aplicando o UUID `id_filial` a todas as tabelas de domínio.

**1. `filial` (Tenant)**
- `id_filial` (PK, UUID)
- `nome`, `cnpj`, `endereco`

**2. `empresa_terceirizada`**
- `id_terceirizada` (PK, UUID)
- `cnpj`, `nome`

**3. `instrumento`**
- `id_instrumento` (PK, UUID)
- `id_filial` (FK - Isolamento por fábrica)
- `tag`, `cor_etiqueta`, `nome`, `area`, `localizacao`
- `id_equipamento_pai` (FK para relacionamento hierárquico)
- `descricao`, `range`, `faixa_uso`
- `criticidade` (Enum: PCC / PPRO)
- `equipamento_critico` (Boolean)
- `unidade_medida`, `numero_sap`, `numero_plano`
- `agendamento_automatico` (Boolean - Parametriza o fluxo de gatilho)

**4. `agendamento_calibracao`**
- `id_agendamento` (PK, UUID)
- `id_instrumento` (FK)
- `id_terceirizada` (FK)
- `data_calibracao_atual`, `data_proxima_calibracao`
- `status` (Enum: PENDENTE_AVISO, AGUARDANDO_APROVACAO, AGENDADO_NA_TERCEIRIZADA, CONCLUIDO, VENCIDO)

**5. `certificado`**
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
2. **Controle de Automação:** Flag no cadastro do instrumento definindo agendamento manual ou fluxo automático (Machine-to-Machine).
3. **Monitoramento Contínuo (Job):** Rotina Background para verificar janelas de vencimento baseadas na data da próxima calibração.
4. **Notificações:** Disparo de alertas aos usuários da respectiva filial sempre que um agendamento entrar no status PENDENTE_AVISO.
5. **Aprovação Manual:** Interface ou endpoint permitindo o "Aceite" do instrumentista em workflows não automatizados.
6. **Recepção de Certificados:** Endpoint (Webhook) dedicado a receber certificados, erro máximo, erro atual e tolerâncias fornecidos pela provedora de calibração.
7. **Integração ERP:** Envio sistemático do pacote de dados da calibração concluída para o ERP central.
8. **Histórico:** Retorno do tracking e log linear de todas as calibrações passadas de um determinado TAG de instrumento.

### Requisitos Não Funcionais (RNF)
1. **Arquitetura Assíncrona:** Separação de fronteiras com DDD, construída sob Spring Boot e mensageria distribuída com Apache Kafka.
2. **Multi-Tenancy:** Forte isolamento por Tenant de forma a prover integridade e segurança de dados caso mais unidades fabris adotem o mesmo ambiente.
3. **Resiliência Externa:** Padrões Circuit Breaker e Retries aplicados às chamadas saíntes para o ERP e Terceirizadas, absorvendo instabilidades das redes (Resilience4j).
4. **Outbox Pattern:** Uso do padrão Transactional Outbox em cima das bases relacionais para garantir a entrega Atomic-Event no Kafka e prevenir vazamentos de dados por falha de broker.
5. **Observabilidade Total:** Distributed Tracing obrigatório, integrando Trace/Span IDs end-to-end, facilitando depurações e leitura centralizada por APMs (ex: Zipkin/Jaeger).
