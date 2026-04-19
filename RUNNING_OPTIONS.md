# ✅ Solução Alternativa - Executar Localmente (Sem Docker)

## 🔍 Diagnóstico

Detectei que há um **MariaDB/MySQL rodando localmente** na porta 3306:
```
/usr/libexec/mariadbd (processo em execução)
```

Isso causa conflito com o Docker Compose. Você tem **2 opções**:

---

## ✅ Opção 1: Usar MySQL/MariaDB Local (MAIS RÁPIDO)

### Passo 1: Criar banco de dados

```bash
# Conectar ao MySQL/MariaDB local
mysql -u root

# Dentro do MySQL:
CREATE DATABASE IF NOT EXISTS zupayments;
EXIT;
```

### Passo 2: Executar a aplicação

```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Configurar variáveis de ambiente
export URL=jdbc:mysql://localhost:3306/zupayments
export USER_BANCO_DADOS=root
export PASSWORD_BANCO=root  # ou sua senha
export EMAIL=seu-email@gmail.com
export SENHA=sua-senha-app
export SECRET=seu-jwt-secret
export TIMEOUT_LIMIT=3600000

# Executar
mvn spring-boot:run
```

### Esperado

```
... Flyway    Validating migrations
... Flyway    Successfully applied 2 migrations
... Started ZupaymentsApplication in X.XXX seconds
...
```

### Verificar

```bash
# Terminal novo
curl -s http://localhost:8080/api/usuarios | jq
```

---

## ✅ Opção 2: Usar Docker (Recomendado para CI/CD)

### Passo 1: Parar MariaDB local

```bash
# Se MariaDB é systemd service
sudo systemctl stop mariadb

# Ou parar processo individual
ps aux | grep mariadbd
sudo kill -9 PID  # substitua PID pelo número
```

### Passo 2: Iniciar Docker Compose

```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa
docker-compose down -v
docker-compose up -d

# Aguardar ~40s
sleep 40

# Verificar
docker-compose ps
docker-compose logs app | tail -20
```

### Verificar

```bash
curl http://localhost:8080/actuator/health
```

---

## 🎯 Recomendação

Para **desenvolvimento local rápido**: **Use Opção 1 (Local)**

Para **testes com CI/CD**: **Use Opção 2 (Docker)**

---

## ⚡ Quick Start - Opção 1 (Local)

```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Criar banco
mysql -u root -e "CREATE DATABASE IF NOT EXISTS zupayments"

# Exportar variáveis
export URL=jdbc:mysql://localhost:3306/zupayments
export USER_BANCO_DADOS=root
export PASSWORD_BANCO=root
export EMAIL=test@gmail.com
export SENHA=senha
export SECRET=secret
export TIMEOUT_LIMIT=3600000

# Rodar
mvn spring-boot:run
```

**Tempo total:** ~15 segundos (compilação + inicialização)

---

## ⚡ Quick Start - Opção 2 (Docker)

```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Parar MariaDB (se necessário)
sudo systemctl stop mariadb

# Parar antigos containers
docker-compose down -v

# Iniciar
docker-compose up -d

# Aguardar
sleep 45

# Verificar
curl http://localhost:8080/actuator/health
```

**Tempo total:** ~50-60 segundos (download + build + inicialização)

---

## 📊 Comparação

| Aspecto | Local | Docker |
|---------|-------|--------|
| Velocidade | ⚡ Rápido | ⏱️ ~50s |
| Setup | Simples | Automático |
| Produção | ❌ Não | ✅ Sim |
| Debugging | ✅ Fácil | 🔧 Complexo |
| Reprodutibilidade | ⚠️ Depende do SO | ✅ Garantido |

---

## 🐛 Troubleshooting

### Erro: "Access denied for user 'root'"

```bash
# Encontrar senha
mysql -u root -p
# Digite a senha, ou deixe em branco se for root sem senha

# Ou criar usuário
mysql -u root -e "CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'root'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost'; FLUSH PRIVILEGES;"
```

### Erro: "Can't connect to local MySQL server"

```bash
# Verificar se está rodando
mysql -u root -e "SELECT 1"

# Se não funcionar, iniciar
sudo systemctl start mariadb  # ou mysql

# Ou com Homebrew
brew services start mysql@8.0
```

### Porta 3306 em uso (para Docker)

```bash
# Parar MariaDB
sudo systemctl stop mariadb

# Ou usar porta diferente em docker-compose.yml
# Alterar: "3306:3306" para "3307:3306"
```

---

## 📝 environment.properties (Alternativa)

Se preferir não usar variáveis de ambiente:

1. Crie `src/main/resources/application-dev.properties`:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.url=jdbc:mysql://localhost:3306/zupayments
spring.datasource.username=root
spring.datasource.password=root
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

2. Rode com profile:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## ✅ Status

Você tem **2 caminhos funcionais**:

1. ✅ **Local**: `mvn spring-boot:run` (~15s)
2. ✅ **Docker**: `docker-compose up -d` (~50s)

Escolha a que preferir e comece! 🚀

