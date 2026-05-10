# Flyway - Gerenciamento de Migrations de Banco de Dados

## O que é Flyway?

Flyway é uma ferramenta de controle de versão para bancos de dados. Ela gerencia e rastreia as mudanças no esquema do banco de dados através de scripts SQL versionados.

## Estrutura de Migrations

Os arquivos de migration estão localizados em:
```
src/main/resources/db/migration/
```

### Convenção de Nomenclatura

Os arquivos devem seguir o padrão:
```
V<VERSION>__<DESCRIPTION>.sql
```

**Exemplos:**
- `V1__Create_Initial_Schema.sql` - Criar tabelas iniciais
- `V2__Insert_Sample_Data.sql` - Inserir dados de exemplo
- `V3__Add_Indexes.sql` - Adicionar índices
- `V4__Add_New_Column.sql` - Adicionar nova coluna

**Regras importantes:**
- Versões devem ser numéricas e incrementais (1, 2, 3...)
- Separar versão da descrição com `__` (dois underscores)
- Usar CamelCase na descrição
- Underscore em vez de espaços

## Migrations Criadas

### V1__Create_Initial_Schema.sql
Cria o esquema inicial do banco com as 7 tabelas:
- **responsaveis** - Responsáveis pelos projetos
- **fornecedores** - Cadastro de fornecedores
- **pedidos_de_compras** - Pedidos de compra
- **notas_fiscais** - Notas fiscais
- **nota_fiscal_pedido_de_compra** - Relacionamento Many-to-Many
- **usuarios** - Usuários do sistema

**Características:**
- Chaves primárias adequadas
- Chaves estrangeiras com referential integrity
- Índices para melhor performance
- Charset UTF-8 para suportar caracteres especiais

### V2__Insert_Sample_Data.sql
Insere dados de exemplo para desenvolvimento e testes:
- 3 Responsáveis
- 3 Fornecedores
- 3 Usuários com diferentes níveis de acesso

## Configuração

No arquivo `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### Explicação das Configurações

| Propriedade | Valor | Explicação |
|-----------|-------|-----------|
| `ddl-auto` | `validate` | Apenas valida o schema (não cria nem atualiza) |
| `baseline-on-migrate` | `true` | Permite usar baseline em bancos existentes |
| `baseline-version` | `0` | Versão baseline inicial |
| `enabled` | `true` | Ativa o Flyway |
| `locations` | `classpath:db/migration` | Diretório das migrations |

## Como Usar

### 1. Criar uma Nova Migration

```bash
# Criar arquivo SQL seguindo a convenção
touch src/main/resources/db/migration/V3__Add_New_Feature.sql
```

**Exemplo de conteúdo:**
```sql
-- V3__Add_New_Feature.sql
ALTER TABLE usuarios ADD COLUMN telefone VARCHAR(25);
CREATE INDEX idx_usuarios_telefone ON usuarios(telefone);
```

### 2. Executar Migrations

As migrations são executadas automaticamente ao iniciar a aplicação:

```bash
# Iniciar aplicação (executa pendente migrations)
mvn spring-boot:run

# Ou com Maven direto
mvn clean package
```

### 3. Verificar Status das Migrations

Você pode verificar quais migrations foram executadas consultando a tabela:
```sql
SELECT * FROM flyway_schema_history;
```

## Boas Práticas

### ✅ Faça

1. **Migrations Idempotentes**
   ```sql
   CREATE TABLE IF NOT EXISTS tabela (...)
   ALTER TABLE tabela ADD COLUMN coluna INT DEFAULT 0;
   ```

2. **Use Transações**
   ```sql
   START TRANSACTION;
   -- suas operações
   COMMIT;
   ```

3. **Incremente versões sequencialmente**
   - V1, V2, V3... (não V1, V3, V5)

4. **Descreva o que faz no nome**
   - ✅ `V5__Add_Email_Notification_Table.sql`
   - ❌ `V5__Update.sql`

5. **Uma responsabilidade por migration**
   - Criar tabelas na V1
   - Adicionar índices na V2
   - Inserir dados na V3

### ❌ Não Faça

1. **Modificar migrations já executadas**
   - Uma vez que a migration foi executada, ela é imutável

2. **DROP TABLE sem backup**
   ```sql
   -- ❌ Evitar
   DROP TABLE usuarios;
   
   -- ✅ Preferir
   ALTER TABLE usuarios RENAME TO usuarios_backup;
   ```

3. **Modificar dados em producción sem planejamento**

4. **Usar números de versão fora de ordem**

5. **Testar migrations apenas em produção**

## Troubleshooting

### Erro: "Schema history table not found"

**Causa:** Primeira execução do Flyway
**Solução:** Execute a migration; a tabela será criada automaticamente

### Erro: "Validate failed"

**Causa:** Mismatch entre migrations e schema atual
**Solução:**
```bash
# Ver histórico
SELECT * FROM flyway_schema_history;

# Se necessário, limpar e recomeçar (apenas em desenvolvimento)
# Backup primeiro!
TRUNCATE TABLE flyway_schema_history;
DROP TABLE usuarios, pedidos_de_compras, etc;
```

### Erro: "Migration not found"

**Causa:** Arquivo não está em `db/migration/`
**Solução:** Verifique o caminho e o nome do arquivo

## Integração com CI/CD

No GitHub Actions, as migrations são executadas automaticamente:

```yaml
- name: Build e Testes
  run: mvn clean verify
  # Flyway executa automaticamente durante a inicialização do Spring
```

## Próximas Migrations

Exemplos de futuras migrations:

```sql
-- V3__Add_Audit_Timestamps.sql
ALTER TABLE fornecedores ADD COLUMN criado_em DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE fornecedores ADD COLUMN atualizado_em DATETIME ON UPDATE CURRENT_TIMESTAMP;

-- V4__Add_Search_Indexes.sql
CREATE INDEX idx_fornecedores_razao_social ON fornecedores(razao_social);
CREATE INDEX idx_usuarios_email_ativo ON usuarios(email, ativo);

-- V5__Add_Payment_History_Table.sql
CREATE TABLE IF NOT EXISTS historico_pagamentos (
    id BINARY(16) PRIMARY KEY,
    pedido_id BINARY(16),
    data_pagamento DATETIME,
    valor DOUBLE,
    CONSTRAINT fk_hist_pedido FOREIGN KEY (pedido_id) 
        REFERENCES pedidos_de_compras(numero_de_pedido)
);
```

## Referências

- [Documentação Oficial Flyway](https://flywaydb.org/documentation/)
- [Flyway com Spring Boot](https://spring.io/blog/2021/04/05/flyway-community-edition-and-spring-boot-2-5)
- [Best Practices](https://flywaydb.org/documentation/learnmore/bestpractices)

