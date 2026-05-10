# Guia de Teste - Validar Migrations

## 📋 Checklist de Validação

### 1. Verificar Dependências
```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Verificar se Flyway está no pom.xml
grep -A2 "flyway-core" pom.xml
grep -A2 "flyway-mysql" pom.xml
grep -A2 "jackson-datatype-jsr310" pom.xml
```

**Resultado esperado:** ✅ Todas as dependências presentes

### 2. Verificar Configuração
```bash
# Verificar application.properties
grep -i flyway src/main/resources/application.properties
```

**Resultado esperado:**
```
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.baseline-on-migrate=true
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### 3. Verificar Arquivos de Migration
```bash
# Listar migrations
ls -la src/main/resources/db/migration/

# Ver conteúdo
cat src/main/resources/db/migration/V1__Create_Initial_Schema.sql | head -20
cat src/main/resources/db/migration/V2__Insert_Sample_Data.sql | head -10
```

**Resultado esperado:** ✅ Ambos os arquivos presentes

## 🧪 Teste 1: Build Maven

```bash
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Compilar projeto
./mvnw clean compile

# Esperado: BUILD SUCCESS
```

## 🧪 Teste 2: Testar com Docker Compose

```bash
# Navegar ao diretório do projeto
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa

# Iniciar com Docker Compose
docker-compose up -d

# Aguardar ~30 segundos para inicialização

# Ver logs da aplicação
docker-compose logs app | tail -50

# Procurar por mensagens do Flyway
docker-compose logs app | grep -i flyway
```

**Esperado na saída:**
```
... Flyway ... Validating migrations
... Flyway ... Successfully validated
... Flyway ... Current schema version: 0
... Flyway ... Schema history table created successfully
... Flyway ... Successfully applied 2 migrations
```

## 🧪 Teste 3: Verificar Banco de Dados

```bash
# Conectar ao MySQL
docker-compose exec mysql mysql -u root -proot zupayments

# Dentro do MySQL:
SHOW TABLES;
SELECT * FROM flyway_schema_history;
SELECT * FROM responsaveis;
SELECT * FROM fornecedores;
SELECT * FROM usuarios;
DESC pedidos_de_compras;
DESC notas_fiscais;
```

**Resultado esperado:**

```sql
-- Tabelas (8 totais)
mysql> SHOW TABLES;
+----------------------------+
| Tables_in_zupayments       |
+----------------------------+
| flyway_schema_history      |
| fornecedores               |
| nota_fiscal_pedido_de_compra |
| notas_fiscais              |
| pedidos_de_compras         |
| responsaveis               |
| usuarios                   |
+----------------------------+

-- Histórico de migrations
mysql> SELECT * FROM flyway_schema_history;
+---------+-------------------+---------------------+---------+
| version | description       | installed_on        | success |
+---------+-------------------+---------------------+---------+
|       1 | Create Initial... | 2026-04-18 11:00:00 |       1 |
|       2 | Insert Sample...  | 2026-04-18 11:00:05 |       1 |
+---------+-------------------+---------------------+---------+

-- Dados de exemplo
mysql> SELECT COUNT(*) FROM responsaveis;
| 3 |

mysql> SELECT COUNT(*) FROM fornecedores;
| 3 |

mysql> SELECT COUNT(*) FROM usuarios;
| 3 |
```

## 🧪 Teste 4: Testar Aplicação

```bash
# Verificar se a aplicação está rodando
curl -s http://localhost:8080/actuator/health | jq

# Esperado:
# {
#   "status": "UP",
#   "components": {
#     "db": {"status": "UP"}
#   }
# }

# Acessar Swagger
curl -s http://localhost:8080/swagger-ui.html | head -20
```

## 🧪 Teste 5: Teste de API

```bash
# Listar responsáveis
curl -X GET http://localhost:8080/api/responsaveis \
  -H "Content-Type: application/json"

# Listar fornecedores
curl -X GET http://localhost:8080/api/fornecedores \
  -H "Content-Type: application/json"

# Listar usuários
curl -X GET http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json"
```

**Esperado:** ✅ Retorna dados de exemplo inseridos

## 🧪 Teste 6: Validação de Schema

```bash
# Dentro do MySQL:
-- Verificar constraints
SELECT CONSTRAINT_NAME, TABLE_NAME 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'zupayments';

-- Verificar índices
SELECT * FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'zupayments' 
ORDER BY TABLE_NAME, SEQ_IN_INDEX;

-- Verificar colunas
DESC responsaveis;
DESC fornecedores;
DESC pedidos_de_compras;
DESC notas_fiscais;
DESC usuarios;
```

## 🧪 Teste 7: Testar Nova Migration (Simulação)

```bash
# 1. Criar novo arquivo
cat > src/main/resources/db/migration/V3__Test_Migration.sql << 'EOF'
-- V3__Test_Migration.sql (TESTE - será revertido)
ALTER TABLE responsaveis ADD COLUMN telefone_test VARCHAR(25);
EOF

# 2. Parar aplicação
docker-compose stop app

# 3. Reiniciar
docker-compose up -d app

# 4. Verificar se V3 foi executada
docker-compose logs app | grep -i "V3"
docker-compose exec mysql mysql -u root -proot zupayments -e "SELECT * FROM flyway_schema_history;"

# 5. Limpar (reverter para documentação)
rm src/main/resources/db/migration/V3__Test_Migration.sql
docker-compose stop app
docker-compose up -d app
```

## 📊 Sumário dos Testes

| # | Teste | Status | Observações |
|---|-------|--------|------------|
| 1 | Build Maven | ✅/❌ | Verificar saída do Maven |
| 2 | Docker Compose | ✅/❌ | Ver logs do Flyway |
| 3 | Banco de Dados | ✅/❌ | 7 tabelas + história |
| 4 | Aplicação | ✅/❌ | Health check |
| 5 | API | ✅/❌ | Retorna dados |
| 6 | Schema | ✅/❌ | Constraints OK |
| 7 | Nova Migration | ✅/❌ | Versionamento |

## 🐛 Troubleshooting durante Testes

### Erro: "Migration not found"
```bash
# Verificar arquivo
ls -la src/main/resources/db/migration/V*.sql

# Verificar conteúdo
head -1 src/main/resources/db/migration/V1__Create_Initial_Schema.sql
```

### Erro: "Validate failed"
```bash
# Ver histórico
docker-compose exec mysql mysql -u root -proot zupayments \
  -e "SELECT * FROM flyway_schema_history;"

# Resetar (⚠️ APENAS EM DESENVOLVIMENTO)
docker-compose exec mysql mysql -u root -proot zupayments \
  -e "TRUNCATE TABLE flyway_schema_history; DROP TABLE usuarios; DROP TABLE pedidos_de_compras; DROP TABLE notas_fiscais; DROP TABLE fornecedores; DROP TABLE responsaveis; DROP TABLE nota_fiscal_pedido_de_compra;"

# Reiniciar
docker-compose stop app && docker-compose up -d app
```

### Erro: "Connection refused"
```bash
# Verificar MySQL
docker-compose logs mysql

# Reiniciar MySQL
docker-compose restart mysql

# Aguardar health check
sleep 10
docker-compose up -d app
```

## ✅ Validação Final

Se todos os testes passarem, você terá:

✅ Flyway configurado  
✅ 2 migrations executadas  
✅ 7 tabelas criadas  
✅ Dados de exemplo inseridos  
✅ Histórico de migrations registrado  
✅ Aplicação funcionando  
✅ APIs retornando dados  

**Parabéns! 🎉 Flyway está totalmente operacional!**

## 📝 Próximos Passos

1. Criar migrations para novas funcionalidades
2. Testar em staging antes de produção
3. Manter histórico de migrations no Git
4. Documentar cada nova migration
5. Revisar com equipe antes de aplicar

## 🔗 Links Úteis

- [FLYWAY.md](FLYWAY.md) - Guia completo
- [MIGRATIONS.md](MIGRATIONS.md) - Guia rápido
- [SETUP_FLYWAY.md](SETUP_FLYWAY.md) - Setup
- [Documentação Oficial](https://flywaydb.org/)

