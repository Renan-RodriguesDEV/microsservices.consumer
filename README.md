# 🏦 Microservices Consumer - Sistema de Gerenciamento de Contas Bancárias

## 📋 Sumário Executivo

**Microservices Consumer** é uma aplicação Spring Boot 4.0.6 desenvolvida em **Java 17** que implementa um sistema completo de gerenciamento de contas bancárias com suporte a autenticação JWT, autorização por papéis, transferências de fundos e integração com fila de mensagens RabbitMQ. A aplicação segue os padrões de arquitetura **layered** (em camadas) com separação clara entre controllers, services, repositories e models.

---

## 🎯 Objetivos Principais

1. **Autenticação e Autorização**: Sistema robusto com JWT tokens de 24h e controle de acesso baseado em papéis (ROLE_USER, ROLE_ADMIN)
2. **Gerenciamento de Contas**: Criar, consultar, atualizar e deletar contas bancárias vinculadas a usuários
3. **Operações de Transferência**: Transferências de fundos entre contas com validações de saldo e registro em banco de dados
4. **Integração Assíncrona**: Processamento de eventos via RabbitMQ para escalabilidade
5. **API RESTful**: Endpoints documentados com Swagger UI (OpenAPI 3.0)
6. **Segurança**: Criptografia BCrypt para senhas, validação de entrada, tratamento centralizado de exceções

---

## 🏗️ Arquitetura do Sistema

### Padrão em Camadas (Layered Architecture)

```
┌─────────────────────────────────────────────────┐
│         REST Controllers (API Gateway)           │
│  ├─ AuthController       (Authentication)       │
│  ├─ UserController       (User Management)      │
│  ├─ ContaController      (Account Management)   │
│  └─ TransacaoController  (Transfers)            │
└────────────────┬────────────────────────────────┘
                 │
┌─────────────────▼────────────────────────────────┐
│         Business Logic (Services)                 │
│  ├─ UserService          (User operations)      │
│  ├─ ContaService         (Account operations)   │
│  ├─ TransacaoService     (Transfer logic)       │
│  └─ TokenService         (JWT management)       │
└────────────────┬────────────────────────────────┘
                 │
┌─────────────────▼────────────────────────────────┐
│    Data Access Layer (JPA Repositories)          │
│  ├─ UserRepository       (User persistence)     │
│  ├─ ContaRepository      (Account persistence)  │
│  └─ TransacaoRepository  (Transfer persistence) │
└────────────────┬────────────────────────────────┘
                 │
┌─────────────────▼────────────────────────────────┐
│  Database (PostgreSQL) + Message Queue (RabbitMQ)│
│  ├─ Tabelas: users, contas, transacoes          │
│  └─ Fila: processamento de transferências       │
└──────────────────────────────────────────────────┘
```

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| **Java** | 17 LTS | Linguagem principal |
| **Spring Boot** | 4.0.6 | Framework web |
| **Spring Security** | 6+ | Autenticação e autorização |
| **Spring Data JPA** | Latest | ORM Hibernate |
| **PostgreSQL** | Latest | Banco de dados relacional |
| **RabbitMQ** | CloudAMQP | Message broker assíncrono |
| **JWT (Auth0)** | 4.5.2 | Tokens seguros |
| **SpringDoc OpenAPI** | 2.5.0 | Documentação Swagger |
| **Maven** | Latest | Build tool |

---

## 📦 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

- ✅ **Java 17 JDK** (ou superior)
- ✅ **Apache Maven 3.8+**
- ✅ **PostgreSQL 12+** (servidor rodando)
- ✅ **RabbitMQ** (CloudAMQP configurado OU RabbitMQ local)
- ✅ **Git** (para clonar o repositório)

---

## 🚀 Guia de Instalação e Execução

### 1️⃣ **Clonar o Repositório**

```bash
git clone https://github.com/Renan-RodriguesDEV/microsservices.consumer.git
cd consumer
```

### 2️⃣ **Configurar Banco de Dados PostgreSQL**

```sql
-- Conectar ao PostgreSQL
psql -U postgres

-- Criar banco de dados
CREATE DATABASE microservice_consumer;

-- Conectar ao novo banco
\c microservice_consumer

-- Tabelas serão criadas automaticamente pelo Hibernate (ddl-auto=update)
```

### 3️⃣ **Configurar Variáveis de Ambiente**

Editar `src/main/resources/application.properties`:

```properties
# ===== Database =====
spring.datasource.url=jdbc:postgresql://localhost:5432/microservice_consumer
spring.datasource.username=postgres
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# ===== Server =====
server.port=8081

# ===== Security =====
auth.jwt.secret=${JWT_SECRET:sua-chave-secreta-super-segura-aqui}

# ===== RabbitMQ =====
spring.rabbitmq.address=amqps://obmylesx:CVoDqa8cYwSs0xXd_esWSyoN3vgsS9Wp@jaragua.lmq.cloudamqp.com/obmylesx
broker.queue.processamento.name=my_queue
```

**Dica**: Para uso local, configure RabbitMQ local:
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### 4️⃣ **Compilar o Projeto**

```bash
mvn clean compile
```

### 5️⃣ **Executar a Aplicação**

```bash
# Opção 1: Usando Maven
mvn spring-boot:run

# Opção 2: Usar IDE (Spring Boot Dashboard)
# Right-click project → Run as → Spring Boot App

# Opção 3: Build JAR e executar
mvn clean package
java -jar target/consumer-0.0.1-SNAPSHOT.jar
```

A aplicação iniciará em: **http://localhost:8081**

### 6️⃣ **Acessar a Documentação da API**

Swagger UI disponível em: **http://localhost:8081/swagger-ui.html**

---

## 🗂️ Estrutura do Projeto

```
consumer/
│
├── src/
│   ├── main/
│   │   ├── java/com/micoservice/consumer/
│   │   │   ├── ConsumerApplication.java              (Main - Spring Boot entry point)
│   │   │   │
│   │   │   ├── config/
│   │   │   │   └── RabbitMQConfig.java              (Configuração de fila RabbitMQ)
│   │   │   │
│   │   │   ├── controllers/                          (REST API Endpoints)
│   │   │   │   ├── AuthController.java              (POST /auth/login, /auth/register)
│   │   │   │   ├── UserController.java              (CRUD /users)
│   │   │   │   ├── ContaController.java             (CRUD /contas)
│   │   │   │   └── TransacaoController.java         (POST /transacoes)
│   │   │   │
│   │   │   ├── consumer/
│   │   │   │   └── RabbitMQConsumer.java            (Message listener assíncrono)
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── requests/
│   │   │   │   │   │   ├── UserLoginDTO.java        (DTO entrada: {username, password})
│   │   │   │   │   │   ├── ContaDTO.java            (DTO entrada: {saldo})
│   │   │   │   │   │   └── TransacaoDTO.java        (DTO entrada: transferências)
│   │   │   │   │   ├── responses/
│   │   │   │   │   │   ├── UserResponseDTO.java     (DTO saída: sem senha)
│   │   │   │   │   │   ├── ContaResponseDTO.java    (DTO saída: conta com user)
│   │   │   │   │   │   └── TransacaoResponseDTO.java (DTO saída: transação)
│   │   │   │   │   ├── enums/
│   │   │   │   │   │   ├── RoleEnum.java            (USER, ADMIN)
│   │   │   │   │   │   └── TipoTransacao.java       (TRANSFERENCIA, DEPOSITO, etc)
│   │   │   │   │
│   │   │   │   ├── model/                            (JPA Entities)
│   │   │   │   │   ├── User.java                    (Usuário + Spring Security)
│   │   │   │   │   ├── Conta.java                   (Conta bancária)
│   │   │   │   │   └── Transacao.java               (Registro de transferências)
│   │   │   │   │
│   │   │   │   ├── repositories/                     (Data Access Layer)
│   │   │   │   │   ├── UserRepository.java          (findByUsername)
│   │   │   │   │   ├── ContaRepository.java         (CRUD)
│   │   │   │   │   └── TransacaoRepository.java     (CRUD)
│   │   │   │   │
│   │   │   │   └── services/                         (Business Logic)
│   │   │   │       ├── UserService.java             (register, login, CRUD)
│   │   │   │       ├── ContaService.java            (conta CRUD)
│   │   │   │       └── TransacaoService.java        (lógica transferência)
│   │   │   │
│   │   │   ├── exceptions/                           (Exception classes)
│   │   │   │   ├── GlobalExceptionHandler.java      (Tratamento centralizado)
│   │   │   │   ├── ResourceNotFound.java            (404)
│   │   │   │   ├── UnauthorizedException.java       (401)
│   │   │   │   └── AlreadyExists.java               (400)
│   │   │   │
│   │   │   └── security/                             (Security configuration)
│   │   │       ├── SecurityConfig.java              (HTTP Security rules)
│   │   │       ├── SecurityFilter.java              (JWT validation filter)
│   │   │       ├── TokenService.java                (JWT generation/validation)
│   │   │       └── AuthConfig.java                  (UserDetailsService)
│   │   │
│   │   └── resources/
│   │       ├── application.properties                (Configurações)
│   │       ├── static/                               (Arquivos CSS, JS, imagens)
│   │       └── templates/                            (Thymeleaf templates, se houver)
│   │
│   └── test/
│       └── java/com/micoservice/consumer/
│           └── ConsumerApplicationTests.java        (Testes unitários)
│
├── pom.xml                                           (Maven dependencies)
├── mvnw, mvnw.cmd                                   (Maven Wrapper - Linux/Windows)
├── README.md                                         (Este arquivo)
└── .gitignore                                        (Git config)
```

---

## 🔌 Documentação da API REST

### Base URL
```
http://localhost:8081
```

### 🔐 **AUTENTICAÇÃO** (Auth Controller)

#### **POST /auth/register**
Registrar novo usuário

**Request Body:**
```json
{
  "username": "joao",
  "password": "senha123"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "username": "joao",
  "createdAt": "2026-05-06"
}
```

**Errors:**
- `400 Bad Request` - Usuário já existe
- `400 Bad Request` - Validação falhou

---

#### **POST /auth/login**
Fazer login e obter JWT token

**Request Body:**
```json
{
  "username": "joao",
  "password": "senha123"
}
```

**Response:** `200 OK`
- Header: `Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

**Errors:**
- `401 Unauthorized` - Credenciais inválidas

---

#### **GET /auth**
Health check da rota de autenticação

**Response:** `200 OK`
```
"Rota de autenticação funcionando!!"
```

---

### 👥 **USUÁRIOS** (User Controller)

**Headers Required:**
```
Authorization: Bearer {jwt_token}
```

#### **GET /users**
Listar todos os usuários

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "username": "joao",
    "createdAt": "2026-05-06"
  },
  {
    "id": 2,
    "username": "maria",
    "createdAt": "2026-05-06"
  }
]
```

---

#### **GET /users/{id}**
Obter detalhes de um usuário específico

**Response:** `200 OK`
```json
{
  "id": 1,
  "username": "joao",
  "createdAt": "2026-05-06"
}
```

**Errors:**
- `404 Not Found` - Usuário não existe
- `401 Unauthorized` - Token inválido/expirado

---

#### **PUT /users/{id}**
Atualizar dados de um usuário

**Request Body:**
```json
{
  "username": "joao_atualizado",
  "password": "nova_senha123"
}
```

**Response:** `200 OK` - Usuário atualizado

---

#### **DELETE /users/{id}**
Deletar um usuário

**Response:** `204 No Content`

---

### 🏧 **CONTAS** (Conta Controller)

#### **GET /contas**
Listar todas as contas

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "saldo": 5000.0,
    "user": {
      "id": 1,
      "username": "joao",
      "createdAt": "2026-05-06"
    }
  }
]
```

---

#### **GET /contas/{id}**
Obter detalhes de uma conta específica

**Response:** `200 OK`
```json
{
  "id": 1,
  "saldo": 5000.0,
  "user": {
    "id": 1,
    "username": "joao",
    "createdAt": "2026-05-06"
  }
}
```

---

#### **POST /contas**
Criar nova conta

**Request Body:**
```json
{
  "saldo": 1000.0
}
```

**Response:** `200 OK` - Conta criada

---

#### **PUT /contas/{id}**
Atualizar saldo da conta

**Request Body:**
```json
{
  "saldo": 2500.0
}
```

**Response:** `200 OK`

---

#### **DELETE /contas/{id}**
Deletar conta (apenas se saldo >= 0)

**Response:** `204 No Content`

---

### 💸 **TRANSFERÊNCIAS** (Transacao Controller)

#### **POST /transacoes**
Realizar transferência entre contas

**Request Body:**
```json
{
  "idOrigem": 1,
  "idDestino": 2,
  "valor": 500.0,
  "tipoTransacao": "TRANSFERENCIA"
}
```

**Response:** `200 OK`
```json
{
  "origem": {
    "id": 1,
    "saldo": 4500.0,
    "user": { ... }
  },
  "destino": {
    "id": 2,
    "saldo": 2500.0,
    "user": { ... }
  },
  "valor": 500.0,
  "tipoTransacao": "TRANSFERENCIA"
}
```

**Errors:**
- `404 Not Found` - Conta origem ou destino não existe
- `401 Unauthorized` - Saldo insuficiente
- `400 Bad Request` - Validação falhou

---

## 🗄️ Diagrama UML - Entidades

```mermaid
erDiagram
    USER ||--|| CONTA : "1:1"
    CONTA ||--o{ TRANSACAO : "1:N"

    USER {
        long id PK
        string username UK
        string password
        enum role "ADMIN, USER"
        date createdAt
        date updatedAt
    }

    CONTA {
        long id PK
        double saldo
        long user_id FK
        date createdAt
        date updatedAt
    }

    TRANSACAO {
        long id PK
        double valor
        enum tipo "TRANSFERENCIA, DEPOSITO, etc"
        long conta_id FK
        datetime createdAt
        datetime updatedAt
    }
```

### Explicação das Entidades:

**USER (Usuário)**
- Implementa `UserDetails` do Spring Security
- Armazena credenciais criptografadas com BCrypt
- Pode ter apenas UMA conta (OneToOne)
- Papéis: ROLE_USER (padrão), ROLE_ADMIN

**CONTA (Conta Bancária)**
- Vinculada a exatamente um User (OneToOne)
- Saldo em Double (⚠️ TODO: converter para BigDecimal em produção)
- Uma conta pode ter múltiplas transferências

**TRANSACAO (Transferência)**
- Registra todas as operações de transferência
- Vinculada a uma Conta (ManyToOne)
- Tipos: TRANSFERENCIA, DEPOSITO, SAQUE

---

## 📊 Diagrama de Casos de Uso

```mermaid
graph TD
    A[Usuario Não Autenticado] -->|Registrar| B[Registrar Nova Conta]
    A -->|Login| C[Autenticar com JWT]
    
    D[Usuario Autenticado] -->|GET /users| E[Listar Usuários]
    D -->|POST /contas| F[Criar Conta]
    D -->|GET /contas| G[Listar Contas]
    D -->|PUT /contas| H[Atualizar Saldo]
    D -->|POST /transacoes| I[Realizar Transferência]
    
    I -->|Validar| J{Saldo Suficiente?}
    J -->|Sim| K[Debitar Origem]
    K --> L[Creditar Destino]
    L --> M[Registrar Transacao]
    M --> N[Enviar para RabbitMQ]
    
    J -->|Não| O[Erro 401 - Saldo Insuficiente]
    
    P[RabbitMQConsumer] -->|Recebe| N
    P -->|Processa| Q[Auditoria/Logging]
```

---

## 🔐 Fluxo de Autenticação e Autorização

```mermaid
sequenceDiagram
    Client->>AuthController: POST /auth/register
    AuthController->>UserService: register(UserLoginDTO)
    UserService->>PasswordEncoder: encode(password)
    UserService->>UserRepository: save(user)
    UserRepository->>PostgreSQL: INSERT INTO users
    UserService->>ContaRepository: save(conta)
    ContaRepository->>PostgreSQL: INSERT INTO contas
    UserService-->>AuthController: User
    AuthController-->>Client: 200 OK {UserResponseDTO}
    
    Client->>AuthController: POST /auth/login
    AuthController->>UserService: login(UserLoginDTO)
    UserService->>AuthenticationManager: authenticate(token)
    AuthenticationManager->>PasswordEncoder: matches(password)
    UserService->>TokenService: generateToken(username)
    TokenService->>JWT: create().sign()
    TokenService-->>UserService: jwt_token
    UserService-->>AuthController: token
    AuthController-->>Client: Header Authorization: {token}
    
    Client->>UserController: GET /users + Authorization header
    UserController->>SecurityFilter: doFilterInternal()
    SecurityFilter->>TokenService: validadeToken(token)
    TokenService->>JWT: verify().getSubject()
    TokenService-->>SecurityFilter: username
    SecurityFilter->>UserRepository: findByUsername(username)
    UserRepository-->>SecurityFilter: UserDetails
    SecurityFilter->>SecurityContextHolder: setAuthentication()
    SecurityFilter->>UserController: FilterChain.doFilter()
    UserController->>UserService: findAll()
    UserService->>UserRepository: findAll()
    UserRepository->>PostgreSQL: SELECT * FROM users
    PostgreSQL-->>UserService: List<User>
    UserService-->>UserController: users
    UserController-->>Client: 200 OK {List<UserResponseDTO>}
```

---

## 🔄 Fluxo de Transferência com RabbitMQ

```mermaid
graph LR
    A[Cliente] -->|POST /transacoes| B[TransacaoController]
    B -->|tranferir| C[TransacaoService]
    C -->|validate| D{Saldo Suficiente?}
    D -->|Não| E[401 Error]
    D -->|Sim| F[Debitar Origem]
    F --> G[Creditar Destino]
    G --> H[Salvar Transacao]
    H --> I[Producer.send]
    I -->|Publica| J[RabbitMQ Queue]
    J -->|Consome| K[RabbitMQConsumer]
    K --> L[Processar Evento]
    L --> M[Logging/Auditoria]
    H --> N[200 OK TransacaoResponseDTO]
    N --> A
```

---

## 🧪 Testes

### Executar todos os testes

```bash
mvn test
```

### Testes disponíveis

```java
@SpringBootTest
class ConsumerApplicationTests {
    
    @Test
    void testCreateUser()          // Teste de criação de usuário
    
    @Test
    void testSaldoDaConta()        // Teste de saldo da conta
}
```

**Status**: ✅ Testes passando com `@SpringBootTest`

---

## 📋 Configurações Importantes

### application.properties

```properties
# ===== Spring Boot =====
spring.application.name=consumer
server.port=8081

# ===== JPA/Hibernate =====
spring.jpa.hibernate.ddl-auto=update          # update|create|validate
spring.jpa.show-sql=true                      # Log SQL queries

# ===== PostgreSQL =====
spring.datasource.url=jdbc:postgresql://localhost:5432/microservice_consumer
spring.datasource.username=postgres
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver

# ===== Security =====
auth.jwt.secret=${JWT_SECRET:default-secret}  # Use variável de ambiente!

# ===== RabbitMQ =====
spring.rabbitmq.address=amqps://...           # CloudAMQP
broker.queue.processamento.name=my_queue
```

### Variáveis de Ambiente Recomendadas

```bash
export JWT_SECRET="sua-chave-super-segura-aqui"
export DATABASE_URL="jdbc:postgresql://localhost:5432/microservice_consumer"
```

---

## 🔒 Boas Práticas Implementadas

✅ **Segurança**
- JWT tokens com 24h de expiração
- Senhas criptografadas com BCrypt (10 rounds)
- CSRF desabilitado (autenticação stateless)
- Spring Security integrado

✅ **Arquitetura**
- Padrão em camadas (layered architecture)
- DTOs para requests/responses (não expor entidades)
- Repositories para persistência
- Services para lógica de negócio

✅ **Validação**
- `@Valid` em endpoints
- `@NotNull`, `@Positive` em DTOs
- Tratamento centralizado de exceções

✅ **API REST**
- Endpoints RESTful corretos
- ResponseEntity para controle HTTP
- Documentação Swagger/OpenAPI

✅ **Tratamento de Exceções**
- GlobalExceptionHandler centralizado
- Exceções customizadas (ResourceNotFound, UnauthorizedException, AlreadyExists)
- Responses estruturados com status, message, timestamp

✅ **Integração Assíncrona**
- RabbitMQ para processamento de eventos
- Producer/Consumer pattern

---

## ⚠️ TODO (Melhorias Futuras)

- [ ] Converter `Double` para `BigDecimal` (operações monetárias)
- [ ] Adicionar `@Transactional` em TransacaoService
- [ ] Implementar paginação em endpoints GET
- [ ] Adicionar autorizações por recurso (user só vê suas próprias contas)
- [ ] Implementar logging com SLF4J Logger
- [ ] Adicionar rate limiting
- [ ] Versioning de API (/v1/users)
- [ ] Testes de integração completos

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📞 Suporte

Para dúvidas ou problemas:
- 📧 Email: seu-email@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/Renan-RodriguesDEV/microsservices.consumer/issues)

---

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👨‍💼 Autor

**Renan Rodrigues**
- GitHub: [@Renan-RodriguesDEV](https://github.com/Renan-RodriguesDEV)
- LinkedIn: [linkedin.com/in/renanrodrigues](https://linkedin.com)

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)

---

**Última atualização**: 6 de maio de 2026
**Versão**: 1.0.0
**Status**: ✅ Em desenvolvimento ativo
