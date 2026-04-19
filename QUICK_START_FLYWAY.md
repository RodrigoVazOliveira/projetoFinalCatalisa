# 🚀 Quick Start - Flyway

## ⚡ Início Rápido em 5 Minutos

### 1️⃣ Verificar Dependências ✅

```bash
# Flyway está no pom.xml?
grep "flyway" pom.xml

# Esperado:
# <artifactId>flyway-core</artifactId>
# <artifactId>flyway-mysql</artifactId>
```

### 2️⃣ Verificar Configuração ✅

```bash
# Propriedades Flyway estão configuradas?
grep -A3 "flyway" src/main/resources/application.properties

# Esperado:
# spring.flyway.enabled=true
# spring.flyway.locations=classpath:db/migration
```

### 3️⃣ Verificar Migrations ✅

```bash
# Arquivos de migration existem?
ls -la src/main/resources/db/migration/

# Esperado:
# V1__Create_Initial_Schema.sql
# V2__Insert_Sample_Data.sql
```

### 4️⃣ Executar Aplicação

**Opção A: Local**
```bash
mvn spring-boot:run
```

**Opção B: Docker Compose**
```bash
docker-compose up -d
```

### 5️⃣ Validar Execução ✅

```bash
# Ver logs
docker-compose logs app | grep -i flyway

# Esperado: "Successfully validated 2 migrations"
```

---

## 🔍 Verificação Pós-Inicialização

```bash
# Conectar ao MySQL
docker-compose exec mysql mysql -u root -proot zupayments

# Ver histórico de migrations
SELECT * FROM flyway_schema_history;

# Ver tabelas criadas
SHOW TABLES;

# Contar dados
SELECT COUNT(*) FROM usuarios;
SELECT COUNT(*) FROM fornecedores;
SELECT COUNT(*) FROM responsaveis;
```

---

## 📝 Criar Nova Migration

```bash
# 1. Criar arquivo
cat > src/main/resources/db/migration/V3__Your_Description.sql << 'EOF'
-- V3__Your_Description.sql
ALTER TABLE usuarios ADD COLUMN telefone VARCHAR(25);
CREATE INDEX idx_usuarios_telefone ON usuarios(telefone);
EOF

# 2. Reiniciar aplicação
docker-compose stop app && docker-compose up -d app

# 3. Verificar
docker-compose logs app | grep -i "V3"
```

---

## 💡 Dicas

| Situação | Comando |
|----------|---------|
| Ver histórico | `SELECT * FROM flyway_schema_history;` |
| Resetar (DEV) | `TRUNCATE TABLE flyway_schema_history; DROP TABLE usuarios; ...` |
| Ver dados de teste | `SELECT * FROM usuarios WHERE email LIKE '%@zupayments.com';` |
| Testar novo schema | `mvn clean verify` |

---

## 🐛 Se algo der errado

1. **Verifique os logs:**
   ```bash
   docker-compose logs app | tail -50
   ```

2. **Verifique MySQL:**
   ```bash
   docker-compose logs mysql
   docker-compose exec mysql mysql -u root -proot -e "SHOW DATABASES;"
   ```

3. **Reinicie tudo:**
   ```bash
   docker-compose down
   docker-compose up -d
   ```

4. **Consulte documentação:**
   - [FLYWAY.md](FLYWAY.md) - Completo
   - [TESTING_FLYWAY.md](TESTING_FLYWAY.md) - Testes
   - [FLYWAY_SUMMARY.md](FLYWAY_SUMMARY.md) - Sumário

---

## ✅ Status

- [x] Flyway configurado
- [x] Migrations criadas (V1, V2)
- [x] Dados de exemplo inseridos
- [x] Documentação completa
- [x] Docker Compose pronto
- [ ] Você testou? 👈 **PRÓXIMO PASSO**

---

**Pronto para começar!** 🎉

