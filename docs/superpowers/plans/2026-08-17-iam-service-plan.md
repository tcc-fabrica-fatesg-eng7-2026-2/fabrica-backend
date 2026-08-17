# IAM Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Identity & Access Management (IAM) Service to handle authentication, RBAC authorization, JWT issuance, and the default admin seed user.

**Architecture:** Spring Boot microservice with Spring Security, JWT stateless sessions, and a relational database for user persistence.

**Tech Stack:** Java, Spring Boot (Web, Security, Data JPA), H2 (for tests), PostgreSQL (prod), jjwt (JSON Web Token), Maven.

**Spec:** `docs/superpowers/specs/2026-08-17-calibration-scheduling-architecture-design.md`

## Global Constraints

- Java version: 17 ou superior
- All WebAPIs must be protected by JWT (Bearer token).
- Senhas no banco devem obrigatoriamente estar em BCrypt.

---

### Task 1: Scaffolding IAM Service e Configurações Base

**Files:**
- Create: `iam-service/pom.xml`
- Create: `iam-service/src/main/java/com/fabrica/iam/IamApplication.java`
- Create: `iam-service/src/main/resources/application.yml`

**Interfaces:**
- Produces: Um microserviço Spring Boot rodando na porta 8081.

- [ ] **Step 1: Criar o pom.xml do IAM Service**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <groupId>com.fabrica</groupId>
    <artifactId>iam-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <dependencies>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
        <dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><scope>runtime</scope></dependency>
        <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-api</artifactId><version>0.11.5</version></dependency>
        <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-impl</artifactId><version>0.11.5</version><scope>runtime</scope></dependency>
        <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-jackson</artifactId><version>0.11.5</version><scope>runtime</scope></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
    </dependencies>
</project>
```
- [ ] **Step 2: Configurar application.yml**
```yaml
server:
  port: 8081
spring:
  application:
    name: iam-service
  datasource:
    url: jdbc:h2:mem:iamdb
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 3600000 # 1 hour
```
- [ ] **Step 3: Criar Classe Main**
```java
package com.fabrica.iam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IamApplication {
    public static void main(String[] args) {
        SpringApplication.run(IamApplication.class, args);
    }
}
```
- [ ] **Step 4: Commit Scaffold**
```bash
git add iam-service/pom.xml iam-service/src/
git commit -m "chore: inicializa modulo iam-service com dependencias spring e jwt"
```

---

### Task 2: Entidades e Seed de Usuário Admin

**Files:**
- Create: `iam-service/src/main/java/com/fabrica/iam/domain/Role.java`
- Create: `iam-service/src/main/java/com/fabrica/iam/domain/Usuario.java`
- Create: `iam-service/src/main/java/com/fabrica/iam/repository/UsuarioRepository.java`
- Create: `iam-service/src/main/java/com/fabrica/iam/config/DataSeeder.java`
- Create: `iam-service/src/test/java/com/fabrica/iam/repository/UsuarioRepositoryTest.java`

**Interfaces:**
- Produces: Tabelas de usuário persistidas e usuário `admin:admin` automaticamente injetado.

- [ ] **Step 1: Write test for Seeder/Repository**
```java
package com.fabrica.iam.repository;
import com.fabrica.iam.domain.Usuario;
import com.fabrica.iam.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository repository;

    @Test
    public void testAdminUserIsSeeded() {
        Usuario admin = repository.findByLogin("admin").orElse(null);
        assertNotNull(admin);
        assertEquals(Role.ADM, admin.getRole());
        assertNull(admin.getIdFilial()); // Admin global
    }
}
```
- [ ] **Step 2: Rodar teste falhando**
`cd iam-service && mvn test -Dtest=UsuarioRepositoryTest` (Falha pois classes não existem)

- [ ] **Step 3: Implementar Entidade, Repo e Seeder**
*(Nota: O executor deve criar Role.java (enum ADM, INSTRUMENTISTA), Usuario.java (@Entity com UUID), UsuarioRepository.java, e o DataSeeder.java (@Component implementando CommandLineRunner que cria o admin via PasswordEncoder caso não exista).*
```java
package com.fabrica.iam.domain;
public enum Role { ADM, INSTRUMENTISTA }
```
```java
package com.fabrica.iam.domain;
import jakarta.persistence.*;
import java.util.UUID;
@Entity
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idUsuario;
    private UUID idFilial;
    @Column(unique = true)
    private String login;
    private String senhaHash;
    @Enumerated(EnumType.STRING)
    private Role role;
    // getters and setters...
}
```
```java
package com.fabrica.iam.repository;
import com.fabrica.iam.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByLogin(String login);
}
```
```java
package com.fabrica.iam.config;
import com.fabrica.iam.domain.*;
import com.fabrica.iam.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataSeeder implements CommandLineRunner {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    public DataSeeder(UsuarioRepository repo, PasswordEncoder encoder) { this.repo = repo; this.encoder = encoder; }
    @Override
    public void run(String... args) {
        if (repo.findByLogin("admin").isEmpty()) {
            Usuario u = new Usuario();
            u.setLogin("admin");
            u.setSenhaHash(encoder.encode("admin"));
            u.setRole(Role.ADM);
            repo.save(u);
        }
    }
}
```
*(Nota: Será necessário criar um PasswordEncoderConfig provendo o BCryptPasswordEncoder)*

- [ ] **Step 4: Rodar teste e validar**
`mvn test -Dtest=UsuarioRepositoryTest` (Expected: PASS)

- [ ] **Step 5: Commit**
`git add iam-service/src/ && git commit -m "feat: adiciona entidade Usuario, Repository e DataSeeder"`

---

### Task 3: Emissão de JWT e Refresh Token

**Files:**
- Create: `iam-service/src/main/java/com/fabrica/iam/security/JwtService.java`
- Create: `iam-service/src/test/java/com/fabrica/iam/security/JwtServiceTest.java`

- [ ] **Step 1: Test JWT Generation**
```java
// Criar teste que verifica se o JwtService gera o token com claims de role e expiração de 1 hora.
```
- [ ] **Step 2: Implementar JwtService**
Usar a biblioteca `jjwt` para assinar o token usando o `jwt.secret` do properties, injetando o subject (login) e claim (role). Adicionar método para refreshToken (que apenas gera um novo token baseado no subject validado).
- [ ] **Step 3: Validar Testes**
- [ ] **Step 4: Commit**
`git commit -m "feat: implementa gerador e validador de JWT"`

---

### Task 4: API de Autenticação (Login)

**Files:**
- Create: `iam-service/src/main/java/com/fabrica/iam/controller/AuthController.java`
- Create: `iam-service/src/main/java/com/fabrica/iam/security/SecurityConfig.java`

- [ ] **Step 1: Configurar Spring Security (SecurityConfig)**
Desabilitar CSRF, configurar session management para STATELESS, e expor endpoint `/api/auth/login` como `.permitAll()`. Todos os outros devem exigir autenticação.

- [ ] **Step 2: Implementar AuthController**
Criar endpoint POST `/api/auth/login` recebendo `{ "login": "...", "senha": "..." }`.
Buscar o usuário no repositório, validar o BCrypt, e retornar o Token JWT e Refresh Token.
Criar endpoint POST `/api/auth/refresh` recebendo o refresh token antigo para gerar um novo.

- [ ] **Step 3: Teste de Integração (MockMvc)**
Escrever teste chamando `/api/auth/login` com admin:admin e esperando HTTP 200 com token JWT no corpo.

- [ ] **Step 4: Commit final da funcionalidade**
`git commit -m "feat: adiciona controller de login e configuracao de segurança"`
