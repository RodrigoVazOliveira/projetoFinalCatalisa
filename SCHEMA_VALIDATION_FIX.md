# 🔧 Solução - Schema Validation Error (Missing Tables)

## ❌ Problema

```
org.hibernate.tool.schema.spi.SchemaManagementException: 
Schema validation: missing table [fornecedores]
```

### Causa

Hibernate está em modo `validate` (verifica se o schema existe), mas:
1. ❌ MySQL não está rodando
2. ❌ Banco de dados é novo e não tem tabelas
3. ❌ Flyway não executou as migrations ainda

---

## ✅ Solução Rápida

### Opção 1: Usar Docker Compose (RECOMENDADO)

```bash
# 1. Parar tudo (se algo estiver rodando)
docker-compose down

# 2. Iniciar com Docker Compose
docker-compose up -d

# 3. Aguardar ~30-40 segundos (MySQL + Flyway)

# 4. Verificar logs
docker-compose logs app | grep -i "flyway\|successfully"

# 5. Acessar aplicação
# http://localhost:8080
```

**Esperado:**
```
INFO ... Flyway ... Validating migrations
INFO ... Flyway ... Successfully applied 2 migrations
INFO ... Application started successfully
```

---

### Opção 2: Instalar e Rodar MySQL Localmente

#### No Linux/Mac

```bash
# Instalar MySQL
brew install mysql@8.0  # Mac
# ou
sudo apt-get install mysql-server  # Linux

# Iniciar MySQL
brew services start mysql@8.0  # Mac
# ou
sudo systemctl start mysql  # Linux

# Criar banco de dados
mysql -u root << 'EOF'
CREATE DATABASE IF NOT EXISTS zupayments;
EOF

# Rodar aplicação
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa
mvn spring-boot:run
```

---

## 🔍 Diagnóstico

### 1. Verificar se MySQL está rodando

```bash
# Com Docker
docker ps | grep mysql
# Esperado: container rodando

# Localmente
mysql -u root -e "SELECT 1"
# Esperado: sucesso sem erro de conexão
```

### 2. Verificar se banco existe

```bash
mysql -u root -e "SHOW DATABASES LIKE 'zupayments'"
# Esperado: database zupayments
```

### 3. Verificar se tabelas foram criadas

```bash
mysql -u root zupayments -e "SHOW TABLES"
# Esperado: fornecedores, usuarios, etc
```

### 4. Verificar histórico de Flyway

```bash
mysql -u root zupayments -e "SELECT * FROM flyway_schema_history"
# Esperado: V1 e V2 executadas
```

---

## 🛠️ Passo a Passo com Docker Compose

### 1. Garantir que Docker está instalado

```bash
docker --version
# Esperado: Docker version X.X.X

docker-compose --version
# Esperado: Docker Compose version X.X.X
```

### 2. Parar qualquer container anterior

```bash
docker-compose down -v  # -v remove volumes
```

### 3. Iniciar Fresh

```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Construir e iniciar
docker-compose up -d

# Ou com rebuild
docker-compose up -d --build
```

### 4. Monitorar inicialização

```bash
# Terminal 1: Ver logs
docker-compose logs -f app

# Terminal 2 (em outro terminal): Aguardar
sleep 40 && docker-compose exec mysql mysql -u root -proot zupayments -e "SHOW TABLES"
```

### 5. Validar Status

```bash
# Verificar containers rodando
docker-compose ps
# Esperado: app (running), mysql (running)

# Verificar saúde
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}

# Acessar API
curl http://localhost:8080/api/usuarios
# Esperado: JSON com dados
```

---

## 📝 application.properties - Configuração Atual

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

**Explicação:**
- `validate` = Apenas valida, não cria tabelas
- `enabled=true` = Flyway ativado
- `locations` = Onde estão as migrations

**Ordem de execução:**
1. **Primeiro:** Flyway executa V1 (cria tabelas)
2. **Depois:** Flyway executa V2 (insere dados)
3. **Então:** Hibernate valida se tabelas existem ✅
4. **Finalmente:** Aplicação inicia

---

## ✅ Checklist de Resolução

- [ ] Docker Compose instalado (`docker-compose --version`)
- [ ] Sem containers MySQL rodando conflitantes (`docker ps`)
- [ ] Arquivo docker-compose.yml existe
- [ ] arquivo .env.example copiado para .env (se necessário)
- [ ] Executado `docker-compose up -d`
- [ ] Aguardado 40 segundos
- [ ] Verificado logs: `docker-compose logs app`
- [ ] Verificado tabelas: `docker-compose exec mysql mysql -u root -proot zupayments -e "SHOW TABLES"`
- [ ] Testado API: `curl http://localhost:8080/api/usuarios`
- [ ] Aplicação respondendo: http://localhost:8080

---

## 🐛 Se Ainda Não Funcionar

### Erro: "Connection refused"

```bash
# MySQL não está respondendo
# Solução:
docker-compose restart mysql
sleep 10
docker-compose up -d app
```

### Erro: "Access denied for user"

```bash
# Credenciais incorretas
# Verificar em docker-compose.yml:
# MYSQL_ROOT_PASSWORD=root
# MYSQL_DATABASE=zupayments

# Testar conexão:
docker-compose exec mysql mysql -u root -proot -e "SELECT 1"
```

### Erro: "No address associated with hostname"

```bash
# Problema de rede Docker
# Solução:
docker-compose down
docker network prune -f
docker-compose up -d
```

### Erro: "Flyway migrations not executed"

```bash
# Verificar logs
docker-compose logs app | grep -i flyway

# Se não aparecer "Successfully applied", Flyway não rodou
# Verificar:
# 1. Flyway dependencies no pom.xml
# 2. Arquivo migrations existem em db/migration/
# 3. Nomes corretos (V1__, V2__)
```

---

## 🎯 Resumo Rápido

### ⚡ 3 Comandos para Resolver

```bash
# 1. Limpar tudo
docker-compose down -v

# 2. Iniciar
docker-compose up -d

# 3. Verificar (aguarde 30s)
docker-compose logs app | grep "successfully\|started"
```

### ✅ Verificação Final

```bash
# Deve retornar dados
curl -s http://localhost:8080/api/usuarios | jq

# Esperado:
# [
#   {
#     "id": "...",
#     "email": "admin@zupayments.com",
#     "nomeCompleto": "Admin Master",
#     ...
#   },
#   ...
# ]
```

---

## 📚 Documentação Relacionada

- 🐳 [DOCKER.md](DOCKER.md) - Setup Docker completo
- ⚡ [QUICK_START_FLYWAY.md](QUICK_START_FLYWAY.md) - Quick start
- 🧪 [TESTING_FLYWAY.md](TESTING_FLYWAY.md) - Testes
- 📋 [FLYWAY.md](FLYWAY.md) - Guia Flyway

---

**Status:** ✅ SOLUÇÃO COMPLETA

O problema é que o banco está vazio. Use Docker Compose para inicializar tudo automaticamente!

