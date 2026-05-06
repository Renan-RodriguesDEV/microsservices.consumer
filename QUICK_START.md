# 🚀 Quick Start Guide - Microservices Consumer

**Tempo estimado**: 5 minutos para estar rodando ✅

---

## ⚡ Pré-requisitos Rápidos

```bash
# Verificar Java 17+
java -version

# Verificar Maven 3.8+
mvn -v

# PostgreSQL rodando em localhost:5432
# RabbitMQ rodando (ou CloudAMQP configurado)
```

---

## 1️⃣ Clone e Setup (2 min)

```bash
# Clone
git clone https://github.com/Renan-RodriguesDEV/microsservices.consumer.git
cd consumer

# Crie banco de dados
psql -U postgres -c "CREATE DATABASE microservice_consumer;"

# Compile
mvn clean compile
```

---

## 2️⃣ Rode a Aplicação (30 seg)

```bash
# Opção A: Maven
mvn spring-boot:run

# Opção B: IDE (Spring Boot Dashboard)
# Right-click project → Run as Spring Boot App
```

**Esperado:**
```
Started ConsumerApplication in 3.5 seconds
Tomcat started on port 8081
```

---

## 3️⃣ Teste a API (2 min)

### Abrir Swagger UI
```
http://localhost:8081/swagger-ui.html
```

### Ou usar cURL/Postman

#### **Registrar Usuário**
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "password": "senha123"
  }'
```

**Resposta:**
```json
{
  "id": 1,
  "username": "joao",
  "createdAt": "2026-05-06"
}
```

---

#### **Fazer Login**
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "password": "senha123"
  }' \
  -v
```

**Procure pelo header:**
```
Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Copie o token (sem "Bearer")**

---

#### **Listar Usuários (com autenticação)**
```bash
TOKEN="seu_token_aqui"

curl -X GET http://localhost:8081/users \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta:**
```json
[
  {
    "id": 1,
    "username": "joao",
    "createdAt": "2026-05-06"
  }
]
```

---

#### **Criar Conta**
```bash
TOKEN="seu_token_aqui"

curl -X POST http://localhost:8081/contas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "saldo": 1000.0
  }'
```

---

#### **Transferência entre Contas**
```bash
TOKEN="seu_token_aqui"

# Primeiro crie 2 contas (1 e 2)
# Depois faça:

curl -X POST http://localhost:8081/transacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "idOrigem": 1,
    "idDestino": 2,
    "valor": 500.0,
    "tipoTransacao": "TRANSFERENCIA"
  }'
```

---

## 📊 Fluxo Completo (Passo a Passo)

```
1. Registre 2 usuários:
   POST /auth/register → user1 ✅
   POST /auth/register → user2 ✅

2. Faça login de ambos e obtenha tokens:
   POST /auth/login (user1) → token1 ✅
   POST /auth/login (user2) → token2 ✅

3. Crie conta para cada user:
   POST /contas {saldo: 5000} (token1) → conta1 ✅
   POST /contas {saldo: 0} (token2) → conta2 ✅

4. Liste contas:
   GET /contas (token1) ✅

5. Realize transferência:
   POST /transacoes {idOrigem:1, idDestino:2, valor:500} ✅

6. Verifique saldos:
   GET /contas/1 (token1) → saldo deve ser 4500 ✅
   GET /contas/2 (token2) → saldo deve ser 500 ✅
```

---

## 🔍 Verificar Logs

**Console da aplicação:**
```
2026-05-06 14:30:15.123  INFO 12345 --- [main] c.m.c.ConsumerApplication : Started
2026-05-06 14:30:16.456  INFO 12345 --- [nio-8081-exec-1] c.m.c.controllers.AuthController : POST /auth/register
2026-05-06 14:30:17.789  INFO 12345 --- [nio-8081-exec-2] c.m.c.consumer.RabbitMQConsumer : Transação enviada para fila
```

---

## 🆘 Troubleshooting

### ❌ "Could not connect to database"
```bash
# Verificar PostgreSQL
psql -U postgres -c "\l"

# Se não existir, criar
createdb microservice_consumer
```

### ❌ "Connection refused - RabbitMQ"
```bash
# Use RabbitMQ local ou configure CloudAMQP
# em application.properties:
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### ❌ "Swagger dá 500"
```bash
# Limpe e recompile
mvn clean compile
mvn spring-boot:run
```

### ❌ Token expirou
```bash
# Faça login novamente para obter novo token
# Tokens expiram em 24h
```

---

## 📝 Estrutura de Resposta

Todas as respostas seguem o padrão:

**Sucesso (200, 201, 204):**
```json
{
  "id": 1,
  "username": "joao",
  "createdAt": "2026-05-06"
}
```

**Erro (400, 401, 404, 500):**
```json
{
  "status code": 400,
  "message": "O saldo não pode ser negativo",
  "timestamp": "2026-05-06T14:30:00.123456"
}
```

---

## 🔐 Headers Necessários

| Endpoint | Header | Exemplo |
|----------|--------|---------|
| `/auth/register` | `Content-Type: application/json` | ✅ Público |
| `/auth/login` | `Content-Type: application/json` | ✅ Público |
| Outros | `Authorization: Bearer {token}` | 🔒 Protegido |

---

## 📚 Documentação Completa

Para mais detalhes, veja:
- [README.md](README.md) - Visão geral completa
- [ARCHITECTURE.md](ARCHITECTURE.md) - Decisões técnicas
- [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) - API Documentation

---

## ✅ Checklist

- [ ] Java 17 instalado
- [ ] Maven compilado sem erros
- [ ] PostgreSQL com banco criado
- [ ] Aplicação rodando em 8081
- [ ] Swagger acessível
- [ ] Registrou usuário com sucesso
- [ ] Login funcionando
- [ ] Token recebido
- [ ] GET /users retorna lista
- [ ] Criou conta com sucesso
- [ ] Transferência completa

**Se todos os itens ✅**, você está pronto! 🎉

---

**Próximo passo**: Leia [README.md](README.md) para documentação completa.

---

*Última atualização: 6 de maio de 2026*
