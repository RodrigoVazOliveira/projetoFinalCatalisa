# 🏦 ZUPayments

Sistema de Gerenciamento de Pagamentos a Fornecedores Fixos

![CI/CD Pipeline](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-85%25-yellowgreen)
![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)

---

## 📋 Objetivo

A Zup possui vários fornecedores fixos (que recebem pagamento todo mês). Atualmente, o processo de verificação de pagamentos de custo fixo é feito **manualmente** através de planilhas.

**Objetivo:** Facilitar o acompanhamento desses pagamentos e automatizar a cobrança da área responsável, evitando que a Zup deixe de realizar pagamentos importantes.

### 🎯 Solução

O sistema mantém uma base centralizada com:
- ✅ Dados dos fornecedores e suas categorias de custo
- ✅ Pedidos de compra associados
- ✅ Responsáveis por cada custo
- ✅ Notas fiscais com acompanhamento automático
- ✅ Envio automático de cobranças quando nota fiscal não é recebida no prazo

---

## ✨ Funcionalidades

### 👥 Responsáveis
- 📝 Cadastro de responsável
- ✅ Ativar ou desativar responsável
- 📋 Listagem de responsáveis

### 🏢 Fornecedores
- 📝 Cadastro de fornecedor
- ✅ Ativar ou desativar fornecedor
- 📊 Listagem por categoria de custo

### 📦 Pedidos de Compra
- 📝 Cadastro de pedido de compra
- ❌ Cancelamento de pedido
- 📊 Relatório de pedidos com responsáveis inativos
- 💰 Listagem de pedidos com saldo disponível

### 📄 Notas Fiscais
- 📝 Cadastro de nota fiscal
- ❌ Cancelamento de nota fiscal
- 📊 Acompanhamento de notas fiscais pendentes

### 📧 Notificações
- 🔔 Envio automático de e-mail para responsáveis
- 📢 Sistema de cobrança automática

---

## 🛠️ Tecnologias

| Tecnologia | Versão |
|-----------|--------|
| Java | 21 LTS |
| Spring Boot | 3.x |
| Maven | 3.9+ |
| JUnit 5 | 5.x |
| Mockito | 5.x |
| Jakarta Mail | 2.x |
| Thymeleaf | 3.x |
| Flyway | Últimas |
| MySQL | 8.0+ |

---

## 🗄️ Banco de Dados

### Flyway - Controle de Versão de Schema

O projeto utiliza **Flyway** para gerenciar migrations de banco de dados de forma versionada e rastreável.

**Migrations criadas:**
- ✅ **V1__Create_Initial_Schema.sql** - Cria 7 tabelas principais
- ✅ **V2__Insert_Sample_Data.sql** - Insere dados de exemplo

**Arquivos de configuração:**
- `FLYWAY.md` - Guia completo sobre Flyway
- `MIGRATIONS.md` - Guia rápido de migrations
- `SETUP_FLYWAY.md` - Setup e sumário
- `TESTING_FLYWAY.md` - Guia de testes

**Estrutura de tabelas:**
```
responsaveis (PK: email)
    ├── pedidos_de_compras (FK: responsavel_email)
    └── notas_fiscais (FK: responsavel_email)

fornecedores (PK: cnpj_ou_cpf)
    ├── pedidos_de_compras (FK: fornecedor_cnpj_ou_cpf)
    └── notas_fiscais (FK: fornecedor_cnpj_ou_cpf)

nota_fiscal_pedido_compra (N:M - linking table)

usuarios (PK: id UUID)
```

Para mais informações, consulte a [Documentação Flyway](FLYWAY.md).

---

## 🚀 Como Usar

### Pré-requisitos
- Java 21 ou superior ⚠️ **OBRIGATÓRIO**
- Maven 3.9 ou superior
- Git
### Instalação

1. **Clone o repositório:**
```bash
git clone <repository-url>
cd projetoFinalCatalisa
```

2. **Compile e instale as dependências:**
```bash
mvn clean install
```

3. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

Acesse: `http://localhost:8080`

### Com Docker Compose

Para executar com Docker (incluindo MySQL):

```bash
# 1. Clonar variáveis de ambiente
cp .env.example .env

# 2. Editar .env com suas credenciais (Gmail, JWT Secret)
nano .env

# 3. Iniciar com Docker Compose
docker-compose up -d

# 4. Acessar a aplicação
# http://localhost:8080
# http://localhost:8080/swagger-ui.html

# 5. Ver logs
docker-compose logs -f app

# 6. Parar aplicação
docker-compose down
```

Para mais detalhes, consulte [DOCKER.md](DOCKER.md)

---

## 📡 API Endpoints

### Responsáveis

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/responsaveis/` | `POST` | Cadastrar novo responsável |
| `/responsaveis/` | `GET` | Listar todos |
| `/responsaveis/{email}` | `GET` | Buscar por email |
| `/responsaveis/?email={email}` | `PATCH` | Ativar/desativar |

### Fornecedores

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/fornecedores/` | `POST` | Cadastrar novo |
| `/fornecedores/` | `GET` | Listar todos |
| `/fornecedores/{cnpjOuCpf}` | `GET` | Buscar por CNPJ/CPF |
| `/fornecedores/?cnpjOuCpf={value}` | `PATCH` | Ativar/desativar |

### Pedidos de Compra

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/pedidos/` | `POST` | Cadastrar novo |
| `/pedidos/` | `GET` | Listar todos |
| `/pedidos/{id}` | `PATCH` | Cancelar pedido |
| `/pedidos/responsaveis?ativo=true` | `GET` | Filtrar por responsável |
| `/pedidos/pendentes?valorMinimo=100&ativo=true&data=01/05/2021` | `GET` | Notas pendentes |

### Notas Fiscais

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/notas-fiscais/` | `POST` | Cadastrar nova |
| `/notas-fiscais/` | `GET` | Listar todas |
| `/notas-fiscais/{id}` | `PATCH` | Cancelar nota |

### Usuários

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/usuarios/` | `POST` | Cadastrar novo |
| `/usuarios/` | `GET` | Listar todos |
| `/usuarios/nivel?idUsuario={id}` | `PATCH` | Alterar nível |
| `/usuarios/ativo?idUsuario={id}` | `PATCH` | Ativar/desativar |

---

## 📝 Exemplos de Requisição

### 1. Cadastro de Responsável

```json
POST /responsaveis/

{
    "email": "rodrigo.silva@zup.com.br",
    "nomeCompleto": "Rodrigo Silva",
    "nomeDoProjeto": "FACILITIES"
}
```

### 2. Cadastro de Fornecedor

```json
POST /fornecedores/

{
    "cnpjOuCpf": "38.551.219/0001-51",
    "razaoSocial": "Empresa XYZ Ltda",
    "logradouro": "Rua Afonso Pena",
    "numero": "25",
    "bairro": "Santa Mônica",
    "cidade": "Uberlândia",
    "estado": "MG",
    "cep": "38400-000",
    "telefone": "(34) 99999-9999",
    "email": "contato@empresa.com.br",
    "categoriaDeCusto": "FACILITIES"
}
```

### 3. Cadastro de Pedido de Compra

```json
POST /pedidos/

{
    "dataDeVencimento": "21/08/2021",
    "valorAproximado": 30000.00,
    "dataDePagamento": "05/05/2021",
    "emailResponsavel": "rodrigo.silva@zup.com.br",
    "dataLimiteEnvio": "15/05/2021",
    "formaDePagamento": "TED_DOC",
    "cnpjOuCpf": "38.551.219/0001-51"
}
```

### 4. Cadastro de Nota Fiscal

```json
POST /notas-fiscais/

{
    "numeroDaNota": 21,
    "cnpjOuCpfFornecedor": "38.551.219/0001-51",
    "valorAPagar": 2000.00,
    "dataDeEmissao": "03/05/2021",
    "pedidoDeCompras": [1, 2, 3],
    "dataDeEnvio": "07/05/2021",
    "emailDoResponsavel": "rodrigo.silva@zup.com.br"
}
```

### 5. Cadastro de Usuário

```json
POST /usuarios/

{
    "email": "admin@zup.com.br",
    "nomeCompleto": "Administrador",
    "senha": "senha_segura_123",
    "nivelDeAcesso": "PERFIL_MASTER"
}
```

---
---

## 📚 Documentação

### Swagger UI (Documentação Interativa)

A documentação da API está disponível em:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

Acesse o arquivo OpenAPI em:
```
http://localhost:8080/v3/api-docs
```

---

## 🔄 CI/CD Pipeline

Este projeto utiliza **GitHub Actions** para automatizar o build, testes e análise de qualidade.

### 📊 Workflows Configurados

#### 1. **CI/CD Pipeline** (`.github/workflows/ci-cd.yml`)

Executado automaticamente em:
- **Push** para `master`
- **Pull Requests** para `master`

**Passos executados:**
1. ✅ Checkout do código
2. ✅ Setup do JDK 21 com cache Maven
3. ✅ Compilação (Maven clean compile)
4. ✅ Execução de testes unitários (Maven test)
5. ✅ Build do projeto (Maven package)
6. ✅ Upload de artefatos (JAR files - 7 dias)
7. ✅ Upload de relatórios de teste (30 dias)
8. ✅ Publicação de resultados dos testes
9. ✅ Comentários automáticos em PRs (sucesso/falha)

**Artefatos Gerados:**
- `build-artifacts/` - Arquivos JAR (mantidos por 7 dias)
- `test-reports/` - Relatórios de teste (mantidos por 30 dias)

#### 2. **Análise de Qualidade** (`.github/workflows/quality-analysis.yml`)

Executado automaticamente em:
- **Push** para `master`
- **Pull Requests** para `master`

**Passos executados:**
1. ✅ Testes com cobertura JaCoCo
2. ✅ Upload para Codecov
3. ✅ Análise SonarQube (opcional)

#### 3. **Feature PR Creator** (`.github/workflows/feature-pr.yml`)

Cria automaticamente Pull Requests para `master` a partir de branches `feature/**`:
- ✅ Valida se PR já existe
- ✅ Cria PR com template automático
- ✅ Adiciona labels automáticos
- ✅ Comenta com orientações

### 🔐 Configuração de Secrets (Opcional)

Para habilitar análise SonarQube, adicione no GitHub:

1. Vá para **Settings** → **Secrets and variables** → **Actions**
2. Clique em **New repository secret**
3. Adicione:

```
SONAR_TOKEN: <seu-token-sonarqube>
SONAR_HOST_URL: <sua-url-sonarqube>
```

### 📈 Como Verificar Status

1. Acesse **Actions** no repositório GitHub
2. Clique no workflow desejado
3. Acompanhe em tempo real

### 📥 Como Baixar Artefatos

1. Vá para **Actions** → Clique na execução
2. Desça até **Artifacts**
3. Clique no artefato para fazer download

### 📚 Documentação Completa dos Workflows

Para mais detalhes, consulte:
```
.github/workflows/README.md
```

---

## 🧪 Testes

O projeto possui testes unitários com **alta cobertura**:

- ✅ **ResponsavelControllerTest** - 4 casos de teste
- ✅ **FornecedorServiceTest** - Testes de lógica
- ✅ **PedidoDeCompraControllerTest** - 5 casos de teste
- ✅ **EmailServiceTest** - Testes de notificações

### Executar testes:

```bash
# Todos os testes
mvn test

# Um teste específico
mvn test -Dtest=ResponsavelControllerTest

# Com relatório de cobertura
mvn clean verify
```

---

## 📁 Estrutura do Projeto

```
projetoFinalCatalisa/
├── .github/
│   └── workflows/
│       ├── ci-cd.yml
│       ├── quality-analysis.yml
│       ├── feature-pr.yml
│       └── README.md
├── src/
│   ├── main/
│   │   ├── java/br/com/zup/zupayments/
│   │   │   ├── controllers/
│   │   │   ├── services/
│   │   │   ├── models/
│   │   │   ├── repositories/
│   │   │   ├── dtos/
│   │   │   ├── enums/
│   │   │   ├── exceptions/
│   │   │   └── mappers/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── mensagens.properties
│   │       └── db/migration/          ← Flyway Migrations
│   │           ├── V1__Create_Initial_Schema.sql
│   │           └── V2__Insert_Sample_Data.sql
│   └── test/java/br/com/zup/zupayments/
├── docs/
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── .env.example
├── DOCKER.md
├── FLYWAY.md
├── MIGRATIONS.md
├── SETUP_FLYWAY.md
├── TESTING_FLYWAY.md
└── README.md
```

---

**Última atualização:** 18 de Abril de 2026

## 📖 Documentação Adicional

- 🐳 [Docker e Containerização](DOCKER.md)
- 🗄️ [Flyway - Migrations do Banco](FLYWAY.md)
- 📋 [Guia Rápido de Migrations](MIGRATIONS.md)
- ⚙️ [Setup Flyway](SETUP_FLYWAY.md)
- 🧪 [Testes de Migrations](TESTING_FLYWAY.md)

## 🤝 Contribuindo

1. Crie uma branch `feature/sua-feature`
2. Commit suas mudanças
3. Push para a branch
4. GitHub Actions criará PR automaticamente
5. Aguarde review e merge

## 📞 Suporte

Para dúvidas ou problemas:
- Consulte a documentação completa nos arquivos `.md`
- Verifique os logs da aplicação
- Acesse o Swagger em `/swagger-ui.html`
