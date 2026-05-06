# 🏗️ Decisões Arquiteturais - Microservices Consumer

## Documento de Decisões Técnicas

Este documento descreve as principais decisões arquiteturais tomadas durante o desenvolvimento do projeto Microservices Consumer, justificando cada escolha tecnológica e padrão implementado.

---

## 📌 Índice

1. [Arquitetura em Camadas](#1-arquitetura-em-camadas)
2. [Separação Request/Response com DTOs](#2-separação-requestresponse-com-dtos)
3. [Implementação de Segurança JWT](#3-implementação-de-segurança-jwt)
4. [Integração RabbitMQ Assíncrona](#4-integração-rabbitmq-assíncrona)
5. [Tratamento Centralizado de Exceções](#5-tratamento-centralizado-de-exceções)
6. [Spring Data JPA vs SQL Puro](#6-spring-data-jpa-vs-sql-puro)

---

## 1. Arquitetura em Camadas

### ✅ Decisão: Implementar Layered Architecture

### 📋 Justificativa

```
┌─ Controllers      (Recebem requisições, delegam para services)
├─ Services        (Lógica de negócio, orquestração)
├─ Repositories    (Abstração de dados, queries)
└─ Models          (Entidades JPA, mapeamento BD)
```

### ✅ Benefícios

| Aspecto | Benefício |
|---------|-----------|
| **Separação de Responsabilidades** | Cada camada tem um propósito específico e bem definido |
| **Testabilidade** | Fácil mockar serviços e repositórios em testes |
| **Manutenibilidade** | Mudanças em uma camada não afetam outras |
| **Reutilização** | Services podem ser usados por diferentes controllers |
| **Escalabilidade** | Preparação para microserviços futuros |

### ❌ Alternativas Descartadas

- **Arquitetura Monolítica Flat**: Difícil manutenção, sem separação clara
- **Arquitetura Hexagonal (Ports & Adapters)**: Complexidade maior para este projeto
- **CQRS**: Overhead desnecessário para este escopo

### 📝 Implementação

```java
// Controllers delegam para Services
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.findAll()); // Delega lógica
    }
}

// Services contêm lógica de negócio
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User register(UserLoginDTO data) {
        // Validações
        if (userRepository.findByUsername(data.username()) != null) {
            throw new AlreadyExists("Usuário já existe");
        }
        
        // Lógica de negócio
        User user = new User();
        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        
        // Persistência delegada
        return userRepository.save(user);
    }
}

// Repositories encapsulam acesso a dados
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

---

## 2. Separação Request/Response com DTOs

### ✅ Decisão: Usar DTOs para Request/Response, NÃO expor entidades

### 📋 Justificativa

**Problema:**
```java
// ❌ RUIM: Retornar entidade expõe tudo
@PostMapping("/register")
public ResponseEntity<User> register(@RequestBody UserLoginDTO data) {
    User user = userService.register(data);
    return ResponseEntity.ok(user);  // Retorna {id, username, password, role, conta, ...}
}
```

Isso expõe:
- ❌ Senha criptografada
- ❌ Campos internos (createdAt, updatedAt)
- ❌ Relacionamentos circulares (User → Conta → User → ∞)
- ❌ Informações desnecessárias para o cliente

**Solução: DTOs**
```java
// ✅ BOM: Retornar DTO sem dados sensíveis
@PostMapping("/register")
public ResponseEntity<UserResponseDTO> register(@RequestBody UserLoginDTO data) {
    User user = userService.register(data);
    UserResponseDTO response = new UserResponseDTO(
        user.getId(), 
        user.getUsername(), 
        user.getCreatedAt()
    );
    return ResponseEntity.ok(response);  // Retorna {id, username, createdAt}
}
```

### ✅ Benefícios

| Aspecto | Benefício |
|---------|-----------|
| **Segurança** | Não expõe campos sensíveis (senha, tokens, etc) |
| **Controle de Versão** | DTOs permitem versionamento de API sem quebrar clientes |
| **Validação de Entrada** | DTOs podem ter validações específicas (@NotNull, @Positive) |
| **Serialização** | Não inclui relacionamentos circulares que causam erros |
| **Documentação** | DTOs deixam explícito o contrato da API |

### 📝 Implementação

```java
// DTO de Entrada (Request)
public record UserLoginDTO(
    @NotNull(message = "Username obrigatório")
    String username,
    
    @NotNull(message = "Senha obrigatória")
    String password
) {}

// DTO de Saída (Response) - sem dados sensíveis
public record UserResponseDTO(
    Long id,
    String username,
    LocalDate createdAt
) {}

// Controller usa DTOs
@PostMapping("/register")
public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserLoginDTO data) {
    User user = userService.register(data);
    return ResponseEntity.ok(mapToDTO(user));
}
```

### 📊 Comparação de Payloads

**Sem DTO (Expõe entidade):**
```json
{
  "id": 1,
  "username": "joao",
  "password": "$2a$10$...",          ❌ Senha
  "role": "USER",                    ⚠️ Implementação interna
  "conta": {
    "id": 1,
    "saldo": 1000,
    "user": {
      "id": 1,
      "username": "joao",            ♻️ Circular!
      ...
    }
  },
  "createdAt": "2026-05-06",
  "updatedAt": "2026-05-06"          ⚠️ Desnecessário
}
```

**Com DTO (Apenas dados necessários):**
```json
{
  "id": 1,
  "username": "joao",
  "createdAt": "2026-05-06"
}
```

---

## 3. Implementação de Segurança JWT

### ✅ Decisão: JWT Stateless + SecurityFilter

### 📋 Justificativa

**Alternativas Consideradas:**

| Opção | Prós | Contras | Escolha |
|-------|------|---------|---------|
| **JWT Stateless** | Escalável, sem sessão no servidor | Token pode ser usado se roubado | ✅ **ESCOLHIDA** |
| **Session Cookies** | Mais seguro, revogável | Não escalável, session hijacking | ❌ |
| **OAuth2/OIDC** | Padrão indústria | Complexidade para MVP | ❌ |

### ✅ Por que JWT?

```
✅ Escalável em microserviços
✅ Não requer estado no servidor
✅ Funciona bem com APIs stateless
✅ Suporta múltiplos clientes (Web, Mobile, IoT)
✅ Pode ser armazenado seguramente no cliente (localStorage com HTTPS)
```

### 🔐 Implementação

**TokenService: Geração e Validação**
```java
@Service
public class TokenService {
    @Value("${jwt.secret:mysecret}")
    private String secretKey;
    
    // Gerar token JWT com 24h de expiração
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
            .withIssuer("consumer-app")
            .withSubject(username)                    // Quem é
            .withExpiresAt(generateExpirationDate())  // Quando expira
            .sign(algorithm);                         // Com que assinatura
        return token;
    }
    
    // Validar token JWT
    public String validadeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String username = JWT.require(algorithm)
            .withIssuer("consumer-app")
            .build()
            .verify(token)      // Valida assinatura
            .getSubject();      // Extrai username
        return username;
    }
    
    private Instant generateExpirationDate() {
        return Instant.now().plusSeconds(24 * 60 * 60); // 24 horas
    }
}
```

**SecurityFilter: Interceptação de Requisições**
```java
@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) 
        throws ServletException, IOException {
        
        // 1. Extrair token do header Authorization
        String token = getToken(request);
        
        if (token != null) {
            try {
                // 2. Validar token e extrair username
                String username = tokenService.validadeToken(token);
                
                // 3. Carregar user do banco
                UserDetails userDetails = userRepository.findByUsername(username);
                
                if (userDetails != null) {
                    // 4. Setar no SecurityContext para requisição
                    UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // Token inválido, continua sem autenticação
                logger.warn("Token inválido: " + e.getMessage());
            }
        }
        
        // 5. Continua o filtro
        filterChain.doFilter(request, response);
    }
    
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            return null;
        return header.replace("Bearer ", "");
    }
}
```

**SecurityConfig: Registrar o Filter**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity
            .csrf(csrf -> csrf.disable())  // JWT não precisa de CSRF
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Sem sessões
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/swagger-ui.html", "/v3/api-docs/**")
                .permitAll()
                .anyRequest()
                .authenticated())  // Todas outras requerem autenticação
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)  // Registra filter
            .build();
    }
}
```

### 🔄 Fluxo Segurança

```
1️⃣ Cliente registra: POST /auth/register {username, password}
   ├─ Controller recebe
   ├─ UserService valida e criptografa senha (BCrypt)
   └─ Salva no BD com password: $2a$10$...

2️⃣ Cliente faz login: POST /auth/login {username, password}
   ├─ Controller recebe
   ├─ AuthenticationManager valida credenciais
   ├─ TokenService gera JWT com expiration de 24h
   └─ Retorna Authorization: Bearer eyJhbGc...

3️⃣ Cliente requisita recurso protegido: GET /users
   ├─ Envia Authorization: Bearer {jwt}
   ├─ SecurityFilter intercepta
   ├─ TokenService valida assinatura
   ├─ Confirma não expirou
   ├─ SecurityContextHolder.setAuthentication()
   └─ Controller executa com usuário autenticado

4️⃣ Se token expirado ou inválido
   ├─ SecurityFilter descarta token
   ├─ Passa requisição sem autenticação
   ├─ @Secured/@PreAuthorize lança AccessDenied
   └─ GlobalExceptionHandler retorna 403 Forbidden
```

---

## 4. Integração RabbitMQ Assíncrona

### ✅ Decisão: Event-Driven com RabbitMQ

### 📋 Justificativa

**Por que processar transferências de forma assíncrona?**

```
Requisição: POST /transacoes {origem, destino, valor}
    │
    ├─ Operação síncrona (ANTES):
    │  └─ Debit, Credit, Log, Auditoria, Notificação = LENTO! ❌
    │
    └─ Operação assíncrona (AGORA):
       ├─ Debit + Credit = rápido ✅ (responde em 100ms)
       └─ Log, Auditoria, Notificação = background ✅ (processa depois)
```

### ✅ Benefícios

```
✅ Resposta rápida ao cliente (operação BD apenas)
✅ Escalável (fila absorve picos)
✅ Desacoplamento (Consumer pode mudar sem afetar API)
✅ Retry automático (RabbitMQ reprocessa falhas)
✅ Auditoria (todos eventos históricos na fila)
```

### 📝 Implementação

**Producer: Publicar evento**
```java
@Service
public class TransacaoService {
    @Autowired
    private Producer producer;
    
    public TransacaoResponseDTO tranferir(TransacaoDTO data) {
        // Validar
        Conta origem = contaService.findById(data.idOrigem());
        Conta destino = contaService.findById(data.idDestino());
        
        if (origem.getSaldo() < data.valor()) {
            throw new UnauthorizedException("Saldo insuficiente");
        }
        
        // Operações síncronas críticas
        origem.setSaldo(origem.getSaldo() - data.valor());
        destino.setSaldo(destino.getSaldo() + data.valor());
        contaService.update(origem.getId(), new ContaDTO(origem.getSaldo()));
        contaService.update(destino.getId(), new ContaDTO(destino.getSaldo()));
        
        // Persiste transação
        Transacao transacao = new Transacao(data.valor(), data.tipoTransacao(), destino);
        transacaoRepository.save(transacao);
        
        // ✅ Publica evento ASSINCRONAMENTE
        producer.send(transacao);  // Não bloqueia!
        
        // Retorna resposta ao cliente imediatamente
        return new TransacaoResponseDTO(...);
    }
}

// Producer
@Service
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void send(Transacao transacao) {
        rabbitTemplate.convertAndSend("processamento_queue", transacao);
        logger.info("Transação enviada para fila: " + transacao.getId());
    }
}
```

**Consumer: Processar evento**
```java
@Component
public class RabbitMQConsumer {
    @RabbitListener(queues = "${broker.queue.processamento.name}")
    public void listenerQueue(Transacao transacao) {
        try {
            logger.info("Processando transação: " + transacao.getId());
            
            // Aqui pode fazer operações demoradas:
            // - Registrar auditoria
            // - Enviar email ao cliente
            // - Integração com sistemas externos
            // - Webhooks
            
            logger.info("Transação processada com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao processar transação", e);
            // RabbitMQ reprocessará a mensagem automaticamente
        }
    }
}
```

**Config: Declarar fila**
```java
@Configuration
public class RabbitMQConfig {
    @Value("${broker.queue.processamento.name}")
    private String queue;
    
    @Bean
    public Queue queue() {
        return new Queue(queue, true);  // durable=true, sobrevive a reboot
    }
    
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();  // Serializar como JSON
    }
}
```

### 📊 Diagrama Fluxo

```
Cliente                API                RabbitMQ             Consumer
   │                   │                     │                   │
   │── POST /transacoes─>                    │                   │
   │                   │                     │                   │
   │                   ├─ Validar            │                   │
   │                   │                     │                   │
   │                   ├─ Debit/Credit       │                   │
   │                   │                     │                   │
   │                   ├─ Save BD            │                   │
   │                   │                     │                   │
   │                   ├─ Enqueue────────────>                   │
   │                   │                     │                   │
   │<─ 200 OK──────────┤                     │                   │
   │   (rápido!)       │                     │                   │
   │                   │                     ├─ Dequeue────────>│
   │                   │                     │                   │
   │                   │                     │                   ├─ Log
   │                   │                     │                   ├─ Email
   │                   │                     │                   ├─ Webhook
   │                   │                     │                   │
   │                   │                     │                   ├─ ACK
   │                   │                     │<─────────────────┤
```

---

## 5. Tratamento Centralizado de Exceções

### ✅ Decisão: GlobalExceptionHandler

### 📋 Justificativa

**Problema sem tratamento centralizado:**
```java
// ❌ Espalhado em cada controller
@PostMapping("/login")
public ResponseEntity<String> login(...) {
    try {
        // lógica
    } catch (AuthenticationException e) {
        return ResponseEntity.status(401).body("Credenciais inválidas");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Erro interno");
    }
}

@PostMapping("/register")
public ResponseEntity<User> register(...) {
    try {
        // lógica
    } catch (AlreadyExists e) {
        return ResponseEntity.status(400).body("Usuário já existe");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Erro interno");
    }
}
// ❌ Código duplicado! Inconsistente!
```

**Solução: GlobalExceptionHandler**
```java
✅ Centralizado, reutilizável, consistente
```

### 📝 Implementação

```java
@ControllerAdvice  // Intercepta exceções de todos os controllers
public class GlobalExceptionHandler {
    
    // Exceção genérica
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Exceção customizada: Recurso não encontrado
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFound e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status code", HttpStatus.NOT_FOUND.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    // Exceção customizada: Não autorizado
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status code", HttpStatus.UNAUTHORIZED.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    // Exceção customizada: Já existe
    @ExceptionHandler(AlreadyExists.class)
    public ResponseEntity<Object> handleAlreadyExists(AlreadyExists e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status code", HttpStatus.BAD_REQUEST.value());
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    // Validação de entrada
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status code", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validação falhou");
        response.put("errors", e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList()));
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
```

### ✅ Benefícios

| Aspecto | Benefício |
|---------|-----------|
| **Consistência** | Mesmo formato de erro para toda a API |
| **Manutenibilidade** | Alterar tratamento em um só lugar |
| **Reutilização** | Handlers servem todos os controllers |
| **DRY** | Evita duplicação de código try-catch |
| **Logging** | Fácil adicionar logging centralizado |

### 📊 Formato de Resposta Padronizado

```json
{
  "status code": 400,
  "message": "O saldo não pode ser negativo",
  "timestamp": "2026-05-06T14:30:00.123456"
}
```

---

## 6. Spring Data JPA vs SQL Puro

### ✅ Decisão: Spring Data JPA + Hibernate

### 📋 Justificativa

| Aspecto | Spring Data JPA | SQL Puro | Escolha |
|---------|-----------------|----------|---------|
| **Velocidade desenvolvimento** | Rápido (1-2 linhas) | Lento (10+ linhas SQL) | ✅ JPA |
| **Portabilidade** | PostgreSQL→MySQL com 0 mudanças | Reescrever queries | ✅ JPA |
| **Type Safety** | Compile-time checking | Runtime errors | ✅ JPA |
| **Performances críticas** | Alguns overheads | Controle total | ❌ SQL |
| **Curva aprendizagem** | Moderada | Baixa | ✅ JPA |

**Para este projeto: JPA é ideal**

### 📝 Implementação

```java
// Spring Data JPA
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);  // ✅ Automático!
}

// Usar
User user = userRepository.findByUsername("joao");
List<User> allUsers = userRepository.findAll();

// vs SQL Puro (mais verboso)
@Repository
public class UserRepositorySQL {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, 
                new Object[]{username},
                new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
```

### ⚠️ Trade-offs JPA

**Vantagens:**
- ✅ Automático mapeamento (annotations)
- ✅ Relacionamentos simplificados (OneToOne, OneToMany)
- ✅ Queries type-safe (evita SQL injection)
- ✅ Lazy/Eager loading

**Desvantagens:**
- ⚠️ N+1 query problem (necessário @EntityGraph)
- ⚠️ Serialização circular (precisou @JsonIgnore)
- ⚠️ Performance em queries complexas
- ⚠️ Overhead em operações simples

### 🎯 Quando trocar para SQL Puro

Se em produção encontrar:
```
- Queries complexas com múltiplos JOINs
- Relatórios com agregações pesadas
- Operações em batch com milhões de registros
- Reports que não precisam de relacionamentos
```

Nesse caso, adicionar método nativo:
```java
@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    // Mantém métodos simples em JPA
    Optional<Transacao> findById(Long id);
    
    // Queries complexas em SQL nativo
    @Query(value = "SELECT t.* FROM transacao t " +
                   "WHERE t.conta_id = :contaId " +
                   "AND t.created_at BETWEEN :dataInicio AND :dataFim " +
                   "ORDER BY t.created_at DESC", 
           nativeQuery = true)
    List<Transacao> findByContaAndDateRange(
        @Param("contaId") Long contaId,
        @Param("dataInicio") LocalDateTime inicio,
        @Param("dataFim") LocalDateTime fim);
}
```

---

## 📊 Resumo de Decisões Arquiteturais

| Decisão | Escolhido | Alternativa | Razão |
|---------|-----------|-------------|-------|
| Padrão Arquitetônico | Layered | Hexagonal, CQRS | Simplicidade |
| Request/Response | DTOs | Entidades | Segurança, Versioning |
| Autenticação | JWT | Sessions, OAuth2 | Scalability, Stateless |
| Processamento Assíncrono | RabbitMQ | Polling, Webhooks | Decoupling, Escalabilidade |
| Tratamento de Exceções | Global Handler | Try-catch inline | Consistência, DRY |
| ORM | Spring Data JPA | SQL Puro, MyBatis | Produtividade |
| Banco de Dados | PostgreSQL | MySQL, MongoDB | ACID, Relações |
| Framework Web | Spring Boot | Quarkus, Micronaut | Maduridade, Comunidade |

---

## 🚀 Próximos Passos Recomendados

### Curto Prazo (1-2 sprints)
- [ ] Converter Double → BigDecimal (operações monetárias)
- [ ] Adicionar @Transactional em TransacaoService
- [ ] Implementar logging com SLF4J

### Médio Prazo (2-3 meses)
- [ ] Paginação em endpoints GET
- [ ] Autorizações por recurso
- [ ] Rate limiting com Redis
- [ ] Testes de integração com TestContainers

### Longo Prazo (3-6 meses)
- [ ] Split em microserviços (Auth, Contas, Transações)
- [ ] Event Sourcing para auditoria
- [ ] CQRS para relatórios pesados
- [ ] GraphQL ao lado de REST

---

**Documento atualizado em**: 6 de maio de 2026
**Versão**: 1.0
**Status**: Aprovado ✅
