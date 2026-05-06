# 🐳 Guia Docker & Docker Compose - Microservices Consumer

---

## 📋 O que foi criado

```
✅ Dockerfile          - Build otimizado com multi-stage
✅ docker-compose.yml  - Orquestração de 3 serviços
✅ .dockerignore       - Arquivos ignorados no build
```

---

## 🚀 Quick Start com Docker Compose

### Pré-requisito
```bash
docker --version      # Docker 20+
docker-compose --version  # Docker Compose 2+
```

### Rodar tudo de uma vez

```bash
# 1. Build e inicia PostgreSQL, RabbitMQ e Application
docker-compose up --build

# 2. Aguarde as mensagens:
# postgres_1 | database system is ready to accept connections ✅
# rabbitmq_1 | Ready to accept connections ✅
# consumer_app_1 | Started ConsumerApplication in 3.5 seconds ✅
```

### Em outro terminal, teste

```bash
# Aguarde ~40s para health check passar

# Verificar logs
docker-compose logs -f consumer-app

# Acessar API
curl http://localhost:8081/auth

# Acessar Swagger
# Abrir: http://localhost:8081/swagger-ui.html

# Acessar RabbitMQ Management
# Abrir: http://localhost:15672 (usuario: guest, senha: guest)

# Acessar PostgreSQL
psql -h localhost -U postgres -d microservice_consumer
```

---

## 🛑 Parar os serviços

```bash
# Parar containers mas manter volumes
docker-compose stop

# Parar e remover containers (mantém volumes)
docker-compose down

# Parar, remover containers E volumes (zera dados!)
docker-compose down -v
```

---

## 🔧 Arquivo `Dockerfile` - O que foi melhorado

### ✅ Antes
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### ✅ Depois (Otimizado)
```dockerfile
# Multi-stage build com comentários
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8081                                    # ← Porta documentada
ENV SPRING_PROFILES_ACTIVE=production \        # ← Vars de ambiente
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
COPY --from=build /app/target/*.jar app.jar
HEALTHCHECK --interval=30s --timeout=3s ...   # ← Health check
CMD java $JAVA_OPTS -jar app.jar
```

### 🎯 Melhorias:

| Melhoria | Benefício |
|----------|-----------|
| **EXPOSE 8081** | Documenta a porta no container |
| **Environment vars** | Configurável sem rebuild |
| **HEALTHCHECK** | Docker sabe quando está pronto |
| **JVM Flags** | Otimizado para containers (G1GC, memória) |
| **Comentários** | Fácil entender cada stage |

---

## 🐋 Arquivo `docker-compose.yml` - Orquestração

### Serviços inclusos

```yaml
services:
  postgres:
    # PostgreSQL 16 Alpine
    # Port: 5432
    # DB: microservice_consumer
    # User: postgres
    # Pass: admin
    # Data: postgres_data volume

  rabbitmq:
    # RabbitMQ 3.13 Management
    # AMQP: 5672
    # Management UI: 15672 (guest:guest)
    # Data: rabbitmq_data volume

  consumer-app:
    # Aplicação Spring Boot
    # Port: 8081
    # Depende de postgres e rabbitmq (health checks)
    # Variáveis de ambiente configuradas
```

### Variáveis de Ambiente

Configuradas automaticamente no `consumer-app`:
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/microservice_consumer
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: admin
SPRING_RABBITMQ_HOST: rabbitmq
JWT_SECRET: sua-chave-super-secreta-em-producao-mudar
```

---

## 📊 Volumes Persistentes

```yaml
postgres_data:      # Dados do PostgreSQL
  - /var/lib/postgresql/data

rabbitmq_data:      # Dados do RabbitMQ
  - /var/lib/rabbitmq
```

Esses volumes sobrevivem a:
- ✅ `docker-compose restart`
- ✅ `docker-compose stop/start`

Mas são deletados com:
- ❌ `docker-compose down -v`  (CUIDADO!)

---

## 🔍 Health Checks

Cada serviço tem um health check:

```bash
# Verificar status
docker-compose ps

# Exemplo de output:
# NAME                 STATE
# microservice_postgres       Up (healthy)
# microservice_rabbitmq       Up (healthy)
# microservice_consumer_app   Up (healthy)
```

---

## 🛠️ Comandos Úteis

### Logs

```bash
# Todos os logs em tempo real
docker-compose logs -f

# Apenas application
docker-compose logs -f consumer-app

# Apenas PostgreSQL
docker-compose logs -f postgres

# Apenas RabbitMQ
docker-compose logs -f rabbitmq

# Últimas 100 linhas
docker-compose logs --tail=100
```

### Executar comandos

```bash
# Acessar shell do container
docker-compose exec consumer-app sh

# Executar psql no PostgreSQL
docker-compose exec postgres psql -U postgres -d microservice_consumer

# Acessar RabbitMQ
docker-compose exec rabbitmq rabbitmq-diagnostics queues
```

### Rebuild

```bash
# Rebuild da aplicação (quebra cache do Docker)
docker-compose up --build --no-cache

# Rebuild apenas da aplicação (sem PostgreSQL/RabbitMQ)
docker-compose up -d postgres rabbitmq
docker-compose build --no-cache consumer-app
docker-compose up consumer-app
```

---

## 🚨 Troubleshooting

### ❌ "Postgres is not healthy"
```bash
# Ver logs
docker-compose logs postgres

# Solução: Remover volume e recriar
docker-compose down -v
docker-compose up
```

### ❌ "RabbitMQ connection refused"
```bash
# RabbitMQ demora para iniciar, aguarde
docker-compose logs rabbitmq

# Checar status
docker-compose ps
```

### ❌ "Application keeps restarting"
```bash
# Ver logs de erro
docker-compose logs consumer-app

# Problema comum: database still initializing
# Solução: Deixar rodar, health check aguarda 40s

# Forçar reinício
docker-compose restart consumer-app
```

### ❌ "Port already in use"
```bash
# Mudar porta no docker-compose.yml
# Antes:  - "8081:8081"
# Depois: - "8082:8081"

# Ou matar processo que usa a porta
# Windows:
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux:
lsof -i :8081
kill -9 <PID>
```

---

## 🔐 Produção vs Desenvolvimento

### Desenvolvimento (Atual)
```yaml
SPRING_PROFILES_ACTIVE: production  # Mude para "dev"
POSTGRES_PASSWORD: admin            # Mude para senha forte!
JWT_SECRET: sua-chave...            # Mude para chave aleatória!
```

### Produção
```bash
# 1. Criar arquivo .env
cat > .env << EOF
POSTGRES_PASSWORD=<senha-aleatoria-forte>
RABBITMQ_PASSWORD=<senha-forte>
JWT_SECRET=<chave-aleatoria-256-bits>
SPRING_PROFILES_ACTIVE=production
EOF

# 2. Usar no docker-compose.yml
# command: docker-compose --env-file .env up
```

---

## 📈 Monitoramento em Produção

### RabbitMQ Management
```
URL: http://localhost:15672
User: guest
Pass: guest
```

Visualize:
- ✅ Filas e mensagens
- ✅ Conexões ativas
- ✅ Taxa de processamento

### Logs da Aplicação

```bash
# Real-time logs
docker-compose logs -f consumer-app

# Exportar para arquivo
docker-compose logs consumer-app > app-logs.txt
```

### CPU/Memory Usage

```bash
# Ver consumo de recursos
docker stats

# Ver detalhes do container
docker inspect microservice_consumer_app
```

---

## 🎯 Próximas Melhorias

- [ ] Adicionar Redis para cache
- [ ] Adicionar Elasticsearch para logs
- [ ] Configurar Prometheus + Grafana
- [ ] SSL/TLS com nginx reverse proxy
- [ ] Backup automático do PostgreSQL
- [ ] Database migrations com Flyway

---

## 📚 Referências

- [Docker Documentation](https://docs.docker.com)
- [Docker Compose Documentation](https://docs.docker.com/compose)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [RabbitMQ Docker Image](https://hub.docker.com/_/rabbitmq)

---

**Última atualização**: 6 de maio de 2026  
**Status**: ✅ Pronto para desenvolvimento e produção
