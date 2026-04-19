# 📊 Guia Rápido - Flyway Migrations

## Estrutura do Banco de Dados

### Tabelas Criadas (V1)

```
┌─────────────────────────────────────┐
│        RESPONSAVEIS (Primary)       │
│  email (PK) | nome_completo | ...   │
└──────────────────┬────────────────────┘
                   │
    ┌──────────────┼──────────────┐
    │              │              │
    ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
│PEDIDOS_      │ │NOTAS_FISCAIS │ │USUARIOS          │
│COMPRAS       │ │              │ │(id, email, ...)  │
└──────────────┘ └──────────────┘ └──────────────────┘
    │              │
    │              └──┐
    │              ┌──┘
    └──────┬───────┘
           │
    ┌──────▼──────────┐
    │FORNECEDORES (PK)│
    │cnpj_ou_cpf      │
    └─────────────────┘
```

### Relacionamentos

| Tabela | Coluna | Referencia | Tipo |
|--------|--------|------------|------|
| pedidos_de_compras | responsavel_email | responsaveis.email | N:1 |
| pedidos_de_compras | fornecedor_cnpj_ou_cpf | fornecedores.cnpj_ou_cpf | N:1 |
| notas_fiscais | responsavel_email | responsaveis.email | N:1 |
| notas_fiscais | fornecedor_cnpj_ou_cpf | fornecedores.cnpj_ou_cpf | N:1 |
| nota_fiscal_pedido_compra | nota_fiscal_id | notas_fiscais.id | N:M |
| nota_fiscal_pedido_compra | pedido_id | pedidos_de_compras.numero_de_pedido | N:M |

## Migrations Aplicadas

✅ **V1__Create_Initial_Schema.sql**
- Cria 7 tabelas do zero
- Define chaves primárias e estrangeiras
- Cria índices para performance

✅ **V2__Insert_Sample_Data.sql**
- Insere 3 responsáveis
- Insere 3 fornecedores
- Insere 3 usuários com diferentes roles

## Próximos Passos

### Para Adicionar Nova Coluna

```sql
-- V3__Add_Phone_To_Responsaveis.sql
ALTER TABLE responsaveis ADD COLUMN telefone VARCHAR(25);
CREATE INDEX idx_responsaveis_telefone ON responsaveis(telefone);
```

### Para Adicionar Nova Tabela

```sql
-- V4__Create_Audit_Log_Table.sql
CREATE TABLE auditoria (
    id BINARY(16) PRIMARY KEY,
    tabela VARCHAR(100),
    operacao VARCHAR(10),
    usuario_id BINARY(16),
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);
```

### Para Alterar Tipo de Coluna

```sql
-- V5__Change_Column_Type.sql
ALTER TABLE pedidos_de_compras MODIFY COLUMN saldo DECIMAL(12,2);
```

## Verificar Status

```bash
# Ver todas as migrations executadas
mysql -u root -p zupayments -e "SELECT * FROM flyway_schema_history;"
```

Resultado esperado:
```
| version | description               | installed_on        | success |
|---------|---------------------------|---------------------|---------|
| 1       | Create Initial Schema     | 2026-04-18 10:00:00 | 1       |
| 2       | Insert Sample Data        | 2026-04-18 10:00:01 | 1       |
```

## Docker Compose

As migrations rodam automaticamente ao iniciar:

```bash
# Iniciar com migrations automáticas
docker-compose up -d

# Ver logs de migração
docker-compose logs app | grep -i flyway
```

## Troubleshooting

### Migrations não estão sendo executadas

1. Verifique se o diretório existe:
   ```bash
   ls -la src/main/resources/db/migration/
   ```

2. Verifique o arquivo application.properties:
   ```properties
   spring.flyway.locations=classpath:db/migration
   ```

3. Reinicie a aplicação

### Conflito de versão

Se receber erro de versão duplicada:
```bash
# Nunca altere uma migration já executada!
# Crie uma nova migration (V3, V4, etc)
```

### Resetar banco em desenvolvimento

```bash
# ⚠️ APENAS EM DESENVOLVIMENTO
# Fazer backup antes!

# Opção 1: Deletar dados
docker-compose exec mysql mysql -u root -proot zupayments \
  -e "TRUNCATE TABLE flyway_schema_history;"

# Opção 2: Recriar banco
docker-compose exec mysql mysql -u root -proot \
  -e "DROP DATABASE zupayments; CREATE DATABASE zupayments;"

# Reiniciar
docker-compose restart app
```

## Links Úteis

- 📖 [Documentação Flyway](https://flywaydb.org/documentation/)
- 📋 [Guia Completo](FLYWAY.md)
- 🐳 [Docker Setup](DOCKER.md)

