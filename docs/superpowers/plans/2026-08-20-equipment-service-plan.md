# Equipment Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Equipment Service to handle the core domain (Filiais, Terceirizadas, Parâmetros e Instrumentos) and emit Kafka events upon asset creation.

**Architecture:** Spring Boot microservice with Data JPA (PostgreSQL) and Spring Kafka.

**Tech Stack:** Java, Spring Boot (Web, Data JPA, Kafka), H2 (for tests), PostgreSQL (prod), Maven.

**Spec:** `docs/superpowers/specs/2026-08-17-calibration-scheduling-architecture-design.md`

## Global Constraints

- Java version: 17 ou superior.
- Multi-tenancy: O sistema deve rastrear e salvar o `id_filial` nos cadastros.
- Kafka: Eventos trafegam em formato JSON.

---

### Task 1: Scaffolding Equipment Service

**Files:**
- Create: `equipment-service/pom.xml`
- Create: `equipment-service/src/main/resources/application.yml`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/EquipmentApplication.java`

**Interfaces:**
- Produces: Microserviço rodando na porta 8082, conectado a um banco de testes H2.

- [ ] **Step 1:** Criar `pom.xml` com as dependências do Spring Web, JPA, Kafka e Testes.
- [ ] **Step 2:** Criar `application.yml` com as configurações do datasource, server port (8082) e tópicos Kafka.
- [ ] **Step 3:** Criar a classe principal anotada com `@SpringBootApplication`.
- [ ] **Step 4:** Testar compilando com `mvn clean compile`.
- [ ] **Step 5:** Commit initial scaffolding.

---

### Task 2: Domínio Base (Filial e EmpresaTerceirizada)

**Files:**
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/Filial.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/EmpresaTerceirizada.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/repository/FilialRepository.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/repository/EmpresaTerceirizadaRepository.java`
- Create: `equipment-service/src/test/java/com/fabrica/equipment/repository/DomainBaseTest.java`

- [ ] **Step 1:** Escrever um teste que tenta salvar e buscar uma Filial e Empresa.
- [ ] **Step 2:** Criar as Entidades usando anotações `@Entity`, mapeando UUID como PK, conforme a spec.
- [ ] **Step 3:** Criar as interfaces do Spring Data JPA (`JpaRepository`).
- [ ] **Step 4:** Rodar o teste e confirmar se passa (GREEN).
- [ ] **Step 5:** Commit.

---

### Task 3: Dicionário de Parâmetros Dinâmicos

**Files:**
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/TipoValor.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/ParametroMetadado.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/ParametroValor.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/ParametroListaValores.java`
- Create: Repositories correspondentes.
- Create: `equipment-service/src/test/java/com/fabrica/equipment/repository/ParametrosTest.java`

- [ ] **Step 1:** Escrever teste validando a inserção de um parâmetro do tipo LISTA e seus valores armazenados.
- [ ] **Step 2:** Implementar `TipoValor` (Enum: NUMERO, LISTA, BOOLEAN, TEXTO).
- [ ] **Step 3:** Implementar as 3 Entidades com seus relacionamentos (OneToMany/ManyToOne). Lembre-se do campo `idFilial` em ParametroValor para o tenant.
- [ ] **Step 4:** Fazer os testes passarem.
- [ ] **Step 5:** Commit.

---

### Task 4: Entidade Core de Instrumento

**Files:**
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/Criticidade.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/domain/Instrumento.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/repository/InstrumentoRepository.java`
- Create: `equipment-service/src/test/java/com/fabrica/equipment/repository/InstrumentoTest.java`

- [ ] **Step 1:** Criar teste que tenta salvar um instrumento completo e validar sua `tag`.
- [ ] **Step 2:** Implementar Enum `Criticidade` (PCC, PPRO) e Entidade `Instrumento` com as colunas definidas no Design (id_filial, cor_etiqueta, nome, area, localizacao, equipamento_pai, etc).
- [ ] **Step 3:** Fazer testes passarem.
- [ ] **Step 4:** Commit.

---

### Task 5: Eventos Kafka (instrument-created-topic)

**Files:**
- Create: `equipment-service/src/main/java/com/fabrica/equipment/service/InstrumentoService.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/controller/InstrumentoController.java`
- Create: `equipment-service/src/main/java/com/fabrica/equipment/messaging/KafkaProducerConfig.java`
- Create: `equipment-service/src/test/java/com/fabrica/equipment/service/InstrumentoServiceKafkaTest.java`

**Interfaces:**
- Produces: JSON payload no tópico `instrument-created-topic` do Kafka (ex: `{ "id": "...", "idFilial": "...", "agendamentoAutomatico": true }`).

- [ ] **Step 1:** Configurar um bean de KafkaTemplate que serialize os objetos em JSON (`KafkaProducerConfig`).
- [ ] **Step 2:** Criar o `InstrumentoService` que injeta o Repository e o `KafkaTemplate`. O método `createInstrumento` deve salvar no banco e disparar um evento.
- [ ] **Step 3:** Escrever um teste unitário mockando o `KafkaTemplate` para verificar se `send()` foi chamado ao criar um instrumento.
- [ ] **Step 4:** Criar o Endpoint no `InstrumentoController` (POST `/api/instrumentos`).
- [ ] **Step 5:** Commit.
