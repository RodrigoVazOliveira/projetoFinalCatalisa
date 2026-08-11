# 📋 Especificação de Desenho de Software (SDD)
## ZUPayments - Sistema de Gerenciamento de Pagamentos a Fornecedores

**Versão:** 1.0.0  
**Data:** 18 de Abril de 2026  
**Status:** ✅ Implementado  
**Autor(es):** Equipe Zup

---

## 📑 Índice

1. [Visão Geral](#visão-geral)
2. [Objetivos do Sistema](#objetivos-do-sistema)
3. [Escopo](#escopo)
4. [Arquitetura Geral](#arquitetura-geral)
5. [Modelos de Dados](#modelos-de-dados)
6. [Componentes do Sistema](#componentes-do-sistema)
7. [Fluxos de Negócio](#fluxos-de-negócio)
8. [Estrutura de Pastas](#estrutura-de-pastas)
9. [Padrões e Conventions](#padrões-e-conventions)
10. [Dependências](#dependências)
11. [Segurança](#segurança)
12. [Performance](#performance)

---

## 🎯 Visão Geral

O **ZUPayments** é um sistema web baseado em **Spring Boot 3.x** (Java 21+) que automatiza o gerenciamento de pagamentos a fornecedores fixos da empresa Zup. 

### O Problema
A Zup possui vários fornecedores que recebem pagamento fixo mensalmente, e esse acompanhamento era realizado **manualmente através de planilhas**, causando atrasos e falhas.

### A Solução
Um sistema centralizado que:
- ✅ Mantém dados atualizados de fornecedores
- ✅ Rastreia pedidos de compra e notas fiscais
- ✅ Notifica responsáveis automaticamente
- ✅ Previne que pagamentos sejam esquecidos

---

## 🎓 Objetivos do Sistema

### Objetivos Funcionais
1. **Gerenciar Fornecedores**: CRUD completo com categorias de custo
2. **Controlar Pedidos de Compra**: Rastrear todos os pedidos associados aos fornecedores
3. **Acompanhar Notas Fiscais**: Verificar status de recebimento e pagamento
4. **Notificar Responsáveis**: Enviar e-mails automáticos de cobrança
5. **Gerar Relatórios**: Identificar pagamentos pendentes

### Objetivos Não-Funcionais
- 🔒 **Segurança**: JWT, Spring Security, validação de entrada
- 📊 **Performance**: Resposta < 200ms para queries comuns
- 🧪 **Qualidade**: Cobertura de testes > 85%
- 📈 **Escalabilidade**: Suporta 10.000+ registros
- 🛠️ **Manutenibilidade**: Código limpo, bem documentado

---

## 📦 Escopo

### O que está incluído ✅
- Cadastro e gerenciamento de entidades (Fornecedores, Responsáveis, Pedidos, Notas)
- Sistema de autenticação e autorização
- Notificações por e-mail
- API RESTful com documentação Swagger
- Migrations de banco de dados (Flyway)
- Testes unitários

### O que NÃO está incluído ❌
- Integração com sistemas de pagamento reais
- Dashboard avançado com gráficos
- Relatórios em PDF/Excel
- App mobile
- Suporte a múltiplas moedas

---

## 🏗️ Arquitetura Geral

### Visão em Camadas (Layered Architecture)

```
┌──────────────────────────────────────────────┐
│         API REST (Controllers)               │
│   Responsáveis pela comunicação HTTP         │
└──────────────────────────────────────────────┘
                      ↓
┌──────────────────────────────────────────────┐
│      DTOs (Data Transfer Objects)            │
│   Validação e transformação de dados         │
└──────────────────────────────────────────────┘
                      ↓
┌──────────────────────────────────────────────┐
│      Services (Lógica de Negócio)            │
│      Controllers chamar services             │
└──────────────────────────────────────────────┘
                      ↓
┌──────────────────────────────────────────────┐
│   Repositories (Acesso a Dados)              │
│     JPA/Hibernate com banco de dados         │
└──────────────────────────────────────────────┘
                      ↓
┌──────────────────────────────────────────────┐
│         MySQL Database                       │
│      Flyway para controle de versão          │
└──────────────────────────────────────────────┘
```

### Padrão MVC Adaptado

```
Controller
    ↓
Service (interfaces com implementações)
    ↓
Repository (Spring Data JPA)
    ↓
Entity (modelo JPA)
    ↓
Database
```

### Stack Tecnológico

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| **Runtime** | Java | 21 LTS |
| **Framework** | Spring Boot | 3.x |
| **ORM** | JPA/Hibernate | Padrão |
| **Banco** | MySQL | 8.0+ |
| **Migrations** | Flyway | Último |
| **Segurança** | Spring Security + JWT | JJWT 0.13.0 |
| **Email** | Jakarta Mail | 2.x |
| **Testes** | JUnit 5 + Mockito | Padrão |
| **API Docs** | Springdoc-OpenAPI | 3.0.3 |

---

## 🗄️ Modelos de Dados

### Diagrama Entidade-Relacionamento (ER)

```
┌─────────────────────┐
│    RESPONSAVEIS     │ (PK: email)
│                     │
│ - email (PK)        │
│ - nomeCompleto      │
│ - nomeDoProjeto     │
│ - ativo             │
└─────────────────────┘
        ↑             ↑
        │             │
       (1)           (1)
        │             │
       (N)           (N)
        │             │
┌───────────────────────────────────┐       ┌──────────────────┐
│   PEDIDOS_DE_COMPRAS              │───→  │  FORNECEDORES    │ (PK: cnpj_ou_cpf)
│                                    │       │                  │
│ - id (PK)                          │       │ - cnpj_ou_cpf    │
│ - data_de_vencimento              │       │ - razao_social   │
│ - valor_aproximado                │       │ - logradouro     │
│ - data_de_pagamento               │       │ - numero         │
│ - responsavel_email (FK)          │       │ - bairro         │
│ - data_limite_envio               │       │ - cidade         │
│ - forma_de_pagamento              │       │ - estado         │
│ - fornecedor_cnpj_ou_cpf (FK)     │       │ - cep            │
│ - cancelado                        │       │ - telefone       │
└───────────────────────────────────┘       │ - email          │
        ↑                                     │ - categoria      │
        │                                     │ - ativo          │
       (N)                                    └──────────────────┘
        │
     (M:N)
        │
        ↓
┌───────────────────────────────────┐
│  NOTA_FISCAL_PEDIDO_COMPRA        │ (Tabela de Junção)
│                                    │
│ - nota_fiscal_id (FK)             │
│ - pedido_compra_id (FK)           │
└───────────────────────────────────┘
        ↑
        │
       (N)
        │
┌───────────────────────────────────┐
│      NOTAS_FISCAIS                │ (PK: id)
│                                    │
│ - id (PK)                          │
│ - numero_da_nota                  │
│ - fornecedor_cnpj_ou_cpf (FK)     │
│ - valor_a_pagar                   │
│ - data_de_emissao                 │
│ - data_de_envio                   │
│ - responsavel_email (FK)          │
│ - cancelada                        │
│ - created_at                       │
│ - updated_at                       │
└───────────────────────────────────┘

┌──────────────────────┐
│      USUARIOS        │ (PK: id UUID)
│                      │
│ - id (UUID)          │
│ - email              │
│ - nome_completo      │
│ - categoria_usuario  │
│ - ativo              │
│ - created_at         │
└──────────────────────┘
```

### Descrição das Entidades

#### **Responsáveis**
- **Propósito**: Rastrear quem é responsável por cada custo/projeto
- **Chave Primária**: `email` (único identificador)
- **Campos**:
  - `email`: String - Email único do responsável
  - `nomeCompleto`: String - Nome completo (ex: "Rodrigo Silva")
  - `nomeDoProjeto`: String - Projeto/departamento (ex: "FACILITIES")
  - `ativo`: Boolean - Se o responsável está ativo no sistema

#### **Fornecedores**
- **Propósito**: Armazenar dados dos fornecedores parceiros
- **Chave Primária**: `cnpjOuCpf` (CNPJ ou CPF formatados)
- **Campos**: Dados cadastrais completos + categoria de custo
- **Exemplo**: Empresa de limpeza, fornecedor de papel, etc.

#### **Pedidos de Compra**
- **Propósito**: Rastrear requisições de compra aos fornecedores
- **Relações**: Vinculado a 1 Fornecedor + 1 Responsável + N Notas Fiscais (M:N)
- **Ciclo de vida**: Ativo → Cancelado
- **Campos importantes**: Data de vencimento, valor aproximado, forma de pagamento

#### **Notas Fiscais**
- **Propósito**: Verificar comprovação de entrega/faturamento
- **Relações**: 1 Fornecedor + 1 Responsável + N Pedidos (M:N)
- **Business Rule**: Uma nota pode estar associada a vários pedidos
- **Status**: Enviada/Cancelada/Paga

#### **Usuários**
- **Propósito**: Gestão de acesso e autenticação no sistema
- **Identificador**: UUID único
- **Campos**: Email, senha (hash), nível de acesso

---

## 🔧 Componentes do Sistema

### Controllers (API REST)

#### **ResponsavelController**
```
Endpoint Base: /responsaveis/

GET    /                  → Listar todos responsáveis
GET    /{email}           → Buscar responsável por email  
POST   /                  → Criar novo responsável
PATCH  /?email={email}    → Ativar/desativar responsável
```
- **Responsabilidade**: Receber requisições HTTP e coordenar chamadas a services
- **Validações**: Email válido, nome não vazio
- **Retornos**: ResponsavelDTO

#### **FornecedorController**
```
Endpoint Base: /fornecedores/

GET    /                           → Listar todos
GET    /{cnpjOuCpf}                → Buscar por CNPJ/CPF
POST   /                           → Criar novo fornecedor
PATCH  /?cnpjOuCpf={value}         → Ativar/desativar fornecedor
GET    /?categoriaDeCusto={cat}    → Filtrar por categoria
```
- **Validações**: CNPJ/CPF válido, campos obrigatórios
- **Categoria**: ENUM (FACILITIES, CONSULTORIA, etc.)

#### **PedidoDeCompraController**
```
Endpoint Base: /pedidos/

GET    /                                              → Listar todos
GET    /{id}                                          → Buscar por ID
POST   /                                              → Criar novo
PATCH  /{id}                                          → Cancelar
GET    /responsaveis?ativo={true/false}              → Filtrar por responsável
GET    /pendentes?valorMinimo=X&ativo=Y&data=DD/MM  → Notas pendentes
```

#### **NotaFiscalController**
```
Endpoint Base: /notas-fiscais/

GET    /                          → Listar todas
GET    /{id}                       → Buscar por ID
POST   /                           → Criar nova nota
PATCH  /{id}                       → Cancelar nota
GET    /pendentes?dias=X           → Notas vencidas há X dias
```

#### **UsuarioController**
```
Endpoint Base: /usuarios/

GET    /                               → Listar todos usuários
GET    /{id}                           → Buscar por ID
POST   /                               → Criar novo usuário
PATCH  /nivel?idUsuario={id}           → Alterar nível de acesso
PATCH  /ativo?idUsuario={id}           → Ativar/desativar usuário
```

### Services (Lógica de Negócio)

#### **ResponsavelService**
```java
// Operações
- criarResponsavel(ResponsavelDTO): Responsavel
- listarTodos(): List<Responsavel>
- buscarPorEmail(email): Responsavel
- ativarDesativar(email, ativo): Responsavel
- validarEmail(email): Boolean
```
**Regras de Negócio**:
- Email deve ser único
- Email deve ser válido
- Responsável ativo não pode ser deletado, apenas desativado

#### **FornecedorService**
```java
// Operações
- criarFornecedor(FornecedorDTO): Fornecedor
- listarTodos(): List<Fornecedor>
- listarPorCategoria(categoria): List<Fornecedor>
- buscarPorCnpjOuCpf(cnpjOuCpf): Fornecedor
- ativarDesativar(cnpjOuCpf, ativo): Fornecedor
- validarCnpjOuCpf(cnpjOuCpf): Boolean
```
**Regras de Negócio**:
- CNPJ/CPF deve ser único e válido
- Fornecedor com pedidos ativos não pode ser deletado
- Fornecedor inativo não pode receber novos pedidos

#### **PedidoDeCompraService**
```java
// Operações
- criarPedido(PedidoDTO): PedidoDeCompra
- listarTodos(): List<PedidoDeCompra>
- buscarPorId(id): PedidoDeCompra
- cancelarPedido(id): PedidoDeCompra
- buscarPorResponsavel(email, ativo): List<PedidoDeCompra>
- buscarPendentes(valorMin, ativo, data): List<PedidoDeCompra>
- calcularSaldoDisponivel(id): BigDecimal
```
**Regras de Negócio**:
- Pedido só pode ser criado com Fornecedor e Responsável ativos
- Pedido cancelado não pode receber notas fiscais
- Data de vencimento deve ser > data de criação
- Valor aproximado deve ser > 0

#### **NotaFiscalService**
```java
// Operações
- criarNotaFiscal(NotaFiscalDTO): NotaFiscal
- listarTodas(): List<NotaFiscal>
- buscarPorId(id): NotaFiscal
- cancelarNota(id): NotaFiscal
- associarPedidos(notaId, pedidoIds): NotaFiscal
- buscarPendentes(dias): List<NotaFiscal>
- notificarResponsavel(notaId): void
```
**Regras de Negócio**:
- Nota fiscal só pode referenciar pedidos do mesmo fornecedor
- Nota cancelada não pode ser restaurada
- Data de envio não pode ser anterior à data de emissão
- Notificação automática se nota vencer sem pagamento

#### **EmailService**
```java
// Operações
- enviarCobranca(notaFiscalId): void
- enviarNotificacao(email, mensagem): void
- gerarTemplateCobranca(notaFiscal): String
```
**Responsabilidades**:
- Enviar e-mails de notificação
- Incluir template Thymeleaf
- Logging de falhas de envio

### Repositories (Acesso a Dados)

```java
// Padrão: Spring Data JPA

ResponsavelRepository extends JpaRepository<Responsavel, String> {
    Optional<Responsavel> findByEmail(String email);
    List<Responsavel> findByAtivo(boolean ativo);
}

FornecedorRepository extends JpaRepository<Fornecedor, String> {
    Optional<Fornecedor> findByCnpjOuCpf(String cnpjOuCpf);
    List<Fornecedor> findByCategoriaDeCusto(CategoriaDeCusto categoria);
    List<Fornecedor> findByAtivo(boolean ativo);
}

PedidoDeCompraRepository extends JpaRepository<PedidoDeCompra, Long> {
    List<PedidoDeCompra> findByResponsavelEmail(String email);
    List<PedidoDeCompra> findByFornecedorCnpjOuCpf(String cnpj);
    List<PedidoDeCompra> findByCancelado(boolean cancelado);
}

NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    List<NotaFiscal> findByFornecedorCnpjOuCpf(String cnpj);
    List<NotaFiscal> findByCancelada(boolean cancelada);
    List<NotaFiscal> findByCreatedAtBefore(LocalDateTime data);
}

UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByCategoriaUsuario(CategoriaUsuario categoria);
}
```

### DTOs (Data Transfer Objects)

#### **ResponsavelDTO**
```java
{
  "email": "string (required, email format)",
  "nomeCompleto": "string (required)",
  "nomeDoProjeto": "string (required)",
  "ativo": "boolean (default: true)"
}
```

#### **FornecedorDTO**
```java
{
  "cnpjOuCpf": "string (required, unique)",
  "razaoSocial": "string (required)",
  "logradouro": "string (required)",
  "numero": "string (required)",
  "bairro": "string (required)",
  "cidade": "string (required)",
  "estado": "string (required, 2 chars)",
  "cep": "string (required)",
  "telefone": "string (required)",
  "email": "string (email format)",
  "categoriaDeCusto": "FACILITIES | CONSULTORIA | etc",
  "ativo": "boolean (default: true)"
}
```

#### **PedidoDeCompraDTO**
```java
{
  "dataDeVencimento": "LocalDate (required)",
  "valorAproximado": "BigDecimal (required, > 0)",
  "dataDePagamento": "LocalDate",
  "emailResponsavel": "string (required, exists)",
  "dataLimiteEnvio": "LocalDate",
  "formaDePagamento": "TED_DOC | BOLETO | etc",
  "cnpjOuCpf": "string (required, exists)"
}
```

#### **NotaFiscalDTO**
```java
{
  "numeroDaNota": "Long (required, unique per fornecedor)",
  "cnpjOuCpfFornecedor": "string (required)",
  "valorAPagar": "BigDecimal (required, > 0)",
  "dataDeEmissao": "LocalDate (required)",
  "pedidoDeCompras": "List<Long> (required, non-empty)",
  "dataDeEnvio": "LocalDate",
  "emailDoResponsavel": "string (required)"
}
```

#### **UsuarioDTO**
```java
{
  "email": "string (required, unique)",
  "nomeCompleto": "string (required)",
  "senha": "string (required, min 8 chars)",
  "nivelDeAcesso": "PERFIL_MASTER | PERFIL_USER | etc",
  "ativo": "boolean (default: true)"
}
```

---

## 🔄 Fluxos de Negócio

### Fluxo 1: Cadastro de Fornecedor e Pedido

```
1. Usuário acessa POST /fornecedores/
   ↓
2. Sistema valida dados (CNPJ/CPF, email, etc)
   ↓
3. Salva fornecedor no banco
   ↓
4. Retorna FornecedorDTO com status 201 Created
   
--- Mais tarde ---

5. Usuário acessa POST /pedidos/
   ↓
6. Sistema valida:
   - Fornecedor existe e está ativo
   - Responsável existe e está ativo
   - Dados obrigatórios preenchidos
   ↓
7. Salva pedido vinculado ao fornecedor/responsável
   ↓
8. Retorna PedidoDeCompraDTO com ID
```

### Fluxo 2: Notificação de Nota Fiscal Vencida

```
1. Nota fiscal criada em 01/05/2025
   ↓
2. Agendador verifica notas vencidas (Job diário)
   ↓
3. Nota com data_limite_envio = 15/05/2025
   Data atual = 16/05/2025 → VENCIDA
   ↓
4. Sistema lê responsável da nota
   ↓
5. Carrega template Thymeleaf com dados da nota
   ↓
6. Envia e-mail de cobrança ao responsável
   ↓
7. Registra em log: "Email enviado para responsavel@email.com"
   ↓
8. (Opcional) Incrementa contador de notificações
```

### Fluxo 3: Cancelamento de Pedido

```
1. Usuário acessa PATCH /pedidos/{id}
   ↓
2. Sistema localiza pedido
   ↓
3. Se pedido já tem nota fiscal:
   - Impede cancelamento (validação de negócio)
   - Retorna erro 409 Conflict
   ↓
4. Se pedido está ativo:
   - Define cancelado = true
   - Atualiza timestamp
   - Retorna PedidoDeCompraDTO
   ↓
5. Log: "Pedido #123 cancelado em 16/06/2026"
```

---

## 📂 Estrutura de Pastas

```
projetoFinalCatalisa/
│
├── 📁 src/main/java/br/com/zup/zupayments/
│   │
│   ├── 📄 ZupaymentsApplication.java          ← Classe principal Spring Boot
│   │
│   ├── 📁 controllers/
│   │   ├── ResponsavelController.java
│   │   ├── FornecedorController.java
│   │   ├── PedidoDeCompraController.java
│   │   ├── NotaFiscalController.java
│   │   └── UsuarioController.java
│   │   └── → Responsáveis por receber requisições HTTP
│   │
│   ├── 📁 services/
│   │   ├── ResponsavelService.java (interface)
│   │   ├── ResponsavelServiceImpl.java
│   │   ├── FornecedorService.java
│   │   ├── FornecedorServiceImpl.java
│   │   ├── PedidoDeCompraService.java
│   │   ├── PedidoDeCompraServiceImpl.java
│   │   ├── NotaFiscalService.java
│   │   ├── NotaFiscalServiceImpl.java
│   │   ├── EmailService.java
│   │   └── → Contêm lógica de negócio
│   │
│   ├── 📁 repositories/
│   │   ├── ResponsavelRepository.java
│   │   ├── FornecedorRepository.java
│   │   ├── PedidoDeCompraRepository.java
│   │   ├── NotaFiscalRepository.java
│   │   ├── UsuarioRepository.java
│   │   └── → Acesso aos dados (JPA)
│   │
│   ├── 📁 models/ (ou entities/)
│   │   ├── Responsavel.java
│   │   ├── Fornecedor.java
│   │   ├── PedidoDeCompra.java
│   │   ├── NotaFiscal.java
│   │   ├── Usuario.java
│   │   └── → Entidades JPA/Hibernate
│   │
│   ├── 📁 dtos/
│   │   ├── ResponsavelDTO.java
│   │   ├── FornecedorDTO.java
│   │   ├── PedidoDeCompraDTO.java
│   │   ├── NotaFiscalDTO.java
│   │   ├── UsuarioDTO.java
│   │   └── → Objetos de transferência de dados
│   │
│   ├── 📁 enums/
│   │   ├── CategoriaDeCusto.java
│   │   ├── FormaDePagamento.java
│   │   ├── CategoriaUsuario.java
│   │   └── → Enumerações para tipos fixos
│   │
│   ├── 📁 exceptions/
│   │   ├── ResourceNotFoundException.java
│   │   ├── ValidationException.java
│   │   └── → Exceções customizadas do projeto
│   │
│   ├── 📁 jwt/
│   │   ├── TokenProvider.java
│   │   ├── JwtTokenPlugin.java
│   │   └── → Gerenciamento de tokens JWT
│   │
│   ├── 📁 configuracoes/ (ou config/)
│   │   ├── SecurityConfig.java
│   │   ├── WebConfig.java
│   │   ├── JpaConfig.java
│   │   └── → Configurações do Spring
│   │
│   ├── 📁 core/
│   │   ├── MailConfig.java
│   │   ├── DatabaseConfig.java
│   │   └── → Configurações de infraestrutura
│   │
│   └── 📁 mappers/ (Opcional)
│       ├── ResponsavelMapper.java
│       └── → Conversão Entity ↔ DTO
│
├── 📁 src/main/resources/
│   ├── application.properties          ← Configs de produção
│   ├── application-test.properties     ← Configs de teste
│   ├── mensagens.properties            ← Mensagens i18n
│   │
│   ├── 📁 db/migration/
│   │   ├── V1__Create_Initial_Schema.sql
│   │   ├── V2__Insert_Sample_Data.sql
│   │   └── → Migrations Flyway
│   │
│   └── 📁 templates/
│       ├── cobranca.html               ← Email template
│       └── → Templates Thymeleaf
│
├── 📁 src/test/java/br/com/zup/zupayments/
│   ├── 📁 controllers/
│   │   ├── ResponsavelControllerTest.java
│   │   ├── FornecedorControllerTest.java
│   │   ├── PedidoDeCompraControllerTest.java
│   │   └── NotaFiscalControllerTest.java
│   │
│   ├── 📁 services/
│   │   ├── ResponsavelServiceTest.java
│   │   ├── FornecedorServiceTest.java
│   │   ├── PedidoDeCompraServiceTest.java
│   │   └── NotaFiscalServiceTest.java
│   │
│   └── → Testes unitários e de integração
│
├── 📄 pom.xml                          ← Config Maven
├── 📄 docker-compose.yml               ← Orquestração containers
├── 📄 Dockerfile                       ← Imagem Docker
├── 📄 README.md                        ← Documentação principal
└── 📄 SDD/
    ├── SDD_ZUPAYMENTS_TEMPLATE.md      ← Este arquivo
    ├── ARQUITETURA.md                  ← Detalhes arquitetônicos
    ├── FLUXOS_NEGOCIO.md               ← Diagramas de fluxo
    ├── GUIA_IMPLEMENTACAO.md           ← Como implementar features
    └── DECISOES_ARQUITETURA.md         ← Justificativa de escolhas
```

---

## 🎨 Padrões e Conventions

### Convenção de Nomenclatura

| Elemento | Convenção | Exemplo |
|----------|-----------|---------|
| **Packages** | Lowercase, descritivo | `controllers`, `services.impl` |
| **Classes** | PascalCase, substantivo | `ResponsavelController`, `EmailService` |
| **Interfaces** | PascalCase | `ResponsavelService` |
| **Métodos** | camelCase, verbo | `criarResponsavel()`, `buscarPorEmail()` |
| **Constantes** | UPPER_SNAKE_CASE | `MAX_RETRIES`, `DEFAULT_TIMEOUT` |
| **Campos/Props** | camelCase | `nomeCompleto`, `dataDeCriacao` |
| **DTOs** | EntityDTO | `ResponsavelDTO`, `FornecedorDTO` |
| **Exceptions** | Terminam em Exception | `ResourceNotFoundException` |
| **SQL Tables** | lowercase, snake_case | `responsaveis`, `pedidos_de_compras` |
| **SQL Columns** | lowercase, snake_case | `nome_completo`, `data_de_criacao` |

### Padrões de Design Utilizados

#### 1. **Dependency Injection (DI)**
```java
@Service
public class ResponsavelServiceImpl implements ResponsavelService {
    
    private final ResponsavelRepository repository;
    
    // Injeção automática pelo Spring
    @Autowired
    public ResponsavelServiceImpl(ResponsavelRepository repository) {
        this.repository = repository;
    }
}
```

#### 2. **Repository Pattern**
```java
// Abstrai acesso aos dados
// Facilita testes com mocks
ResponsavelRepository extends JpaRepository<Responsavel, String>
```

#### 3. **DTO Pattern**
```java
// Separa representação externa (API) da interna (DB)
ResponsavelDTO → Responsavel (Entity)
Responsavel → ResponsavelDTO
```

#### 4. **Service Pattern**
```java
// Centraliza lógica de negócio
// Controllers chamar Services
// Services chamar Repositories
Controllers → Services → Repositories
```

#### 5. **Builder Pattern (Lombok)**
```java
@Builder
public class Responsavel {
    // Gera método builder()
    // Responsavel resp = Responsavel.builder()
    //                       .email("test@zup.com")
    //                       .nomeCompleto("Test")
    //                       .build();
}
```

### Tratamento de Erros e Validação

```java
// Validação em DTOs (Bean Validation)
@NotNull(message = "Email não pode ser nulo")
@Email(message = "Email inválido")
private String email;

// Validação em Services (lógica de negócio)
if (responsavel == null) {
    throw new ResourceNotFoundException("Responsável não encontrado");
}

// Tratamento em Controllers (GlobalExceptionHandler)
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<?> handleNotFound(...) {
    return ResponseEntity.status(404).body(erro);
}
```

---

## 📦 Dependências Principais

### Build & Runtime
- **Spring Boot**: Web, Data JPA, Security, Mail, Validation
- **Jakarta/javax**: Mail, Jakarta Bean Validation
- **Hibernate**: JPA implementation

### Banco de Dados
- **Flyway**: Migration de schema
- **MySQL JDBC**: Driver MySQL/MariaDB
- **H2**: Database em memória para testes

### Segurança
- **Spring Security**: Autenticação e autorização
- **JJWT**: JSON Web Tokens
- **OWASP Dependency Check**: Verificação de CVEs

### API & Documentação
- **Springdoc-OpenAPI**: Swagger/Swagger UI
- **Thymeleaf**: Templates HTML para emails

### Testes
- **JUnit 5**: Framework de testes
- **Mockito**: Mocking de objetos
- **Spring Security Test**: Testes com autenticação

### Monitoramento
- **JaCoCo**: Cobertura de testes
- **Spring Boot Actuator**: Health checks

---

## 🔒 Segurança

### Autenticação
- **Estratégia**: JWT Token (JSON Web Token)
- **Fluxo**: 
  1. Usuário faz login com email + senha
  2. Sistema valida credenciais
  3. Gera token JWT assinado
  4. Client inclui token em Authorization header (`Bearer token_aqui`)
  5. Servidor valida token em cada request

### Autorização
- **Spring Security**: Controla acesso por roles
- **Níveis**: PERFIL_MASTER, PERFIL_USER, PERFIL_ADMIN
- **Estratégia**: Roles por método (ex: `@PreAuthorize("hasRole('ADMIN')")`)

### Validação de Entrada
- **Bean Validation**: Anotações em DTOs
- **Type Checking**: Tipos estritos (BigDecimal para valores monetários)
- **SQL Injection Prevention**: Parameterized queries (JPA)

### Criptografia
- **Senhas**: Hash com Spring Security (BCryptPasswordEncoder)
- **JWT**: Assinado com chave secreta

---

## ⚡ Performance

### Índices de Banco de Dados
```sql
-- Primary Keys (automático)
ALTER TABLE responsaveis ADD PRIMARY KEY (email);
ALTER TABLE fornecedores ADD PRIMARY KEY (cnpj_ou_cpf);

-- Índices para busca frequente
ALTER TABLE pedidos_de_compras ADD INDEX idx_responsavel (responsavel_email);
ALTER TABLE pedidos_de_compras ADD INDEX idx_fornecedor (fornecedor_cnpj_ou_cpf);
ALTER TABLE notas_fiscais ADD INDEX idx_fornecedor (fornecedor_cnpj_ou_cpf);
ALTER TABLE notas_fiscais ADD INDEX idx_data_emissao (data_de_emissao);
```

### Lazy Loading & Eager Loading
```java
// Lazy Loading (padrão) - carrega sob demanda
@ManyToOne(fetch = FetchType.LAZY)
private Fornecedor fornecedor;

// Eager Loading - carrega junto
@ManyToOne(fetch = FetchType.EAGER)
private Responsavel responsavel;
```

### Paginação
```java
// Em Repository
Page<Responsavel> findAll(Pageable pageable);

// Em Controller
public ResponseEntity<?> listarTodos(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(service.listarTodos(pageable));
}
```

### Cache (Opcional - Future)
```java
@Cacheable("responsaveis")
public Responsavel buscarPorEmail(String email) {
    return repository.findByEmail(email).orElseThrow(...);
}
```

---

## 📊 Métricas de Qualidade

- **Cobertura de Testes**: 85%+ (JaCoCo)
- **Build Time**: < 2 minutos
- **Container Image Size**: < 500MB
- **Response Time Médio**: < 200ms
- **Uptime Target**: 99.5%
- **CVEs Críticas/Altas**: 0

---

## 🚀 Próximos Passos (Future Roadmap)

- [ ] Implementar caching com Redis
- [ ] Adicionar agendador (Spring Scheduler) para notificações
- [ ] Integração com gateway de pagamento
- [ ] Dashboard web React
- [ ] Relatórios em PDF/Excel
- [ ] Webhooks para sistemas externos
- [ ] Suporte a múltiplas moedas

---

**Versão SDD:** 1.0.0  
**Última Atualização:** 18 de Abril de 2026  
**Status:** ✅ Documento Ativo

