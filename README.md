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
- **Push** para `main` ou `develop`
- **Pull Requests** para `main` ou `develop`

**Passos executados:**
1. ✅ Checkout do código
2. ✅ Setup do JDK 17 com cache Maven
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
- **Push** para `main` ou `develop`
- **Pull Requests** para `main` ou `develop`

**Passos executados:**
1. ✅ Testes com cobertura JaCoCo
2. ✅ Upload para Codecov
3. ✅ Análise SonarQube (opcional)

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
│       └── README.md
├── src/
│   ├── main/java/br/com/zup/zupayments/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── models/
│   │   ├── repositories/
│   │   ├── dtos/
│   │   ├── exceptions/
│   │   └── mappers/
│   └── test/java/br/com/zup/zupayments/
├── pom.xml
└── README.md
```

---

**Última atualização:** 17 de Abril de 2026
