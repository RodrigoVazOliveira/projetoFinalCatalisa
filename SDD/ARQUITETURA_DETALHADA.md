# 🏛️ ARQUITETURA DETALHADA
## ZUPayments - Componentes e Integração

**Versão:** 1.0.0  
**Data:** 18 de Abril de 2026

---

## 📑 Índice

1. [Arquitetura em Camadas](#arquitetura-em-camadas)
2. [Fluxo de Requisição HTTP](#fluxo-de-requisição-http)
3. [Injeção de Dependências](#injeção-de-dependências)
4. [Validação de Dados](#validação-de-dados)
5. [Tratamento de Exceções](#tratamento-de-exceções)
6. [Transações de Banco](#transações-de-banco)
7. [Integração com E-mail](#integração-com-e-mail)
8. [Autenticação JWT](#autenticação-jwt)

---

## 🏗️ Arquitetura em Camadas

### Visão Conceitual

```
┌─────────────────────────────────────────────────────┐
│              REST API / HTTP Clients                │
│           (Swagger UI, Postman, Web, etc)           │
└────────────────────┬────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│          CONTROLLERS (org.springframework.web)       │
│  - Recebem requisições HTTP                         │
│  - Validam formato/tipos                            │
│  - Delegam para Services                            │
│  - Retornam ResponseEntity com status HTTP          │
│  - Exemplo: ResponsavelController                   │
└────────────────────┬────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│   DTOs (Data Transfer Objects)                      │
│  - Validação com @NotNull, @Email, etc             │
│  - Desacoplam API da estrutura interna             │
│  - Permitem versionamento de API                    │
│  - Exemplo: ResponsavelDTO                          │
└────────────────────┬────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│      SERVICES (org.springframework.stereotype)       │
│  - Implementam lógica de negócio                    │
│  - Validam regras de negócio                        │
│  - Coordenam múltiplos repositories                 │
│  - Transações ACID                                  │
│  - Exemplo: ResponsavelServiceImpl                   │
└────────────────────┬────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│     REPOSITORIES (Spring Data JPA)                  │
│  - Abstração do acesso a dados                      │
│  - Queries automáticas e customizadas               │
│  - Conversão Entity ↔ SQL                           │
│  - Exemplo: ResponsavelRepository                   │
└────────────────────┬────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│    ENTITIES / Models (javax.persistence)            │
│  - Mapeamento Objeto-Relacional (ORM)              │
│  - Anotações JPA/Hibernate                          │
│  - Refletem tabelas do banco                        │
│  - Exemplo: Responsavel.java                        │
└────────────────────┬────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│       Hibernate → SQL → MySQL Database              │
│  - Transações gerenciadas                           │
│  - Pooling de conexões                              │
│  - Migrations com Flyway                            │
└─────────────────────────────────────────────────────┘
```

### Detalhamento por Camada

#### **1. Camada de Apresentação (Web)**

```java
@RestController
@RequestMapping("/responsaveis")
public class ResponsavelController {
    
    private final ResponsavelService service;
    
    @PostMapping("/")
    public ResponseEntity<?> criar(@RequestBody @Valid ResponsavelDTO dto) {
        // 1. Recebe dados
        // 2. Validação automática (Bean Validation)
        // 3. Delega para service
        Responsavel responsavel = service.criar(dto);
        
        // 4. Converte de volta para DTO
        // 5. Retorna com status HTTP 201 Created
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ResponsavelDTO(responsavel));
    }
}
```

#### **2. Camada de DTOs**

```java
@Data  // Lombok: getter, setter, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelDTO {
    
    @NotNull(message = "Email não pode ser nulo")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 150)
    private String nomeCompleto;
    
    @NotBlank(message = "Nome do projeto é obrigatório")
    private String nomeDoProjeto;
    
    @PositiveOrZero(message = "Ativo deve ser true ou false")
    private Boolean ativo = true;
}
```

#### **3. Camada de Negócio (Services)**

```java
public interface ResponsavelService {
    Responsavel criar(ResponsavelDTO dto);
    Responsavel buscar(String email);
    List<Responsavel> listar();
    void ativarDesativar(String email, Boolean ativo);
}

@Service
@Slf4j  // Lombok logger
public class ResponsavelServiceImpl implements ResponsavelService {
    
    private final ResponsavelRepository repository;
    
    @Autowired
    public ResponsavelServiceImpl(ResponsavelRepository repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional  // Gerencia transação ACID
    public Responsavel criar(ResponsavelDTO dto) {
        
        // 1. Validação de regra de negócio
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ValidationException("Email já existe");
        }
        
        // 2. Converte DTO para Entity
        Responsavel responsavel = new Responsavel();
        responsavel.setEmail(dto.getEmail());
        responsavel.setNomeCompleto(dto.getNomeCompleto());
        responsavel.setNomeDoProjeto(dto.getNomeDoProjeto());
        responsavel.setAtivo(dto.getAtivo());
        
        // 3. Salva no banco
        Responsavel saved = repository.save(responsavel);
        
        // 4. Log da operação
        log.info("Responsável criado: {}", saved.getEmail());
        
        // 5. Retorna entity
        return saved;
    }
}
```

#### **4. Camada de Dados (Repository)**

```java
@Repository
public interface ResponsavelRepository 
    extends JpaRepository<Responsavel, String> {
    
    // Query automática derivada do método
    Optional<Responsavel> findByEmail(String email);
    
    // Query customizada com JPQL
    @Query("SELECT r FROM Responsavel r WHERE r.ativo = :ativo")
    List<Responsavel> buscarAtivos(@Param("ativo") boolean ativo);
    
    // Query nativa SQL
    @Query(value = "SELECT * FROM responsaveis WHERE nome_completo LIKE %:nome%",
           nativeQuery = true)
    List<Responsavel> buscarPorNome(@Param("nome") String nome);
}
```

#### **5. Camada de Dados (Entities)**

```java
@Entity
@Table(name = "responsaveis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responsavel {
    
    @Id
    @Column(name = "email")
    private String email;
    
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;
    
    @Column(name = "nome_do_projeto", nullable = false)
    private String nomeDoProjeto;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;
    
    // Relacionamento - Um responsável tem muitos pedidos
    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL)
    private List<PedidoDeCompra> pedidos = new ArrayList<>();
    
    // Relacionamento - Um responsável tem muitas notas
    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL)
    private List<NotaFiscal> notas = new ArrayList<>();
}
```

---

## 🔄 Fluxo de Requisição HTTP

### Exemplo: Criar Responsável

```
CLIENTE (Postman/Browser/Frontend)
    │
    ├─ Prepara JSON
    │  {
    │    "email": "rodrigo@zup.com.br",
    │    "nomeCompleto": "Rodrigo Silva",
    │    "nomeDoProjeto": "FACILITIES",
    │    "ativo": true
    │  }
    │
    ├─ Envia POST /responsaveis/
    │  Headers: Content-Type: application/json
    │
    └─────────────────────────────────────────────────────┐
                                                           │
                                    SPRING BOOT SERVER    │
                                                           │
                ┌──────────────────────────────────────────┤
                │ 1. DispatcherServlet recebe request      │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 2. Handler Mapping localiza controller   │
                │    ResponsavelController.criar()         │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 3. Argument Resolver converte JSON       │
                │    → ResponsavelDTO (via ObjectMapper)   │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 4. Validator executa Bean Validation     │
                │    @NotNull, @Email, @Size, etc         │
                │    Se falhar: retorna erro 400           │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 5. Injector passa dependências           │
                │    ResponsavelService injetado           │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 6. Método criar() executado              │
                │    @Transactional abre transação         │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 7. Service valida regras de negócio      │
                │    if (email_existe) throw Exception     │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 8. Repository.save() chamado             │
                │    Hibernate gera INSERT SQL             │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 9. SQL enviado ao JDBC                   │
                │    Pooling de conexões gerencia request  │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 10. INSERT executado no MySQL            │
                │     Transação commitada                  │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 11. Entity retornado com ID populado     │
                │     Hibernate carrega generated ID       │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 12. Service converte Entity → DTO        │
                │     return new ResponsavelDTO(entity)    │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 13. Response Handler (HttpMessageConverter)
                │     DTO → JSON via Jackson ObjectMapper  │
                └──────────────────────────────────────────┤
                                           ↓              │
                ┌──────────────────────────────────────────┤
                │ 14. ResponseEntity(201 CREATED)          │
                │     Headers + Body JSON                  │
                │     CORS headers se necessário           │
                └──────────────────────────────────────────┘
                                           ↓
                        RESPOSTA HTTP
                        
Status: 201 Created
Headers:
  - Content-Type: application/json
  - X-Total-Count: 1
  
Body:
{
  "email": "rodrigo@zup.com.br",
  "nomeCompleto": "Rodrigo Silva",
  "nomeDoProjeto": "FACILITIES",
  "ativo": true
}

CLIENTE recebe resposta
  └─ Processa JSON
     └─ Exibe sucesso na UI
```

---

## 💉 Injeção de Dependências

### IoC Container do Spring

```java
// Configuração
@Configuration
public class AppConfig {
    
    @Bean
    public ResponsavelRepository responsavelRepository() {
        return new JpaRepository(...); // Spring cria automaticamente
    }
    
    @Bean
    public ResponsavelService responsavelService(
        ResponsavelRepository repository
    ) {
        return new ResponsavelServiceImpl(repository);
    }
}

// Uso em Controller
@RestController
public class ResponsavelController {
    
    private final ResponsavelService service;
    
    // Spring injeta automaticamente a dependência
    @Autowired
    public ResponsavelController(ResponsavelService service) {
        this.service = service;
    }
}
```

### Ciclo de Vida do Bean

```
1. Instantiation     → Spring cria instância
2. Population        → Propriedades preenchidas
3. BeanAware         → setBeanName(), setBeanFactory() (se houver)
4. BeanPostProcessor → postProcessBeforeInitialization()
5. Initialization    → @PostConstruct ou init-method
6. BeanPostProcessor → postProcessAfterInitialization()
7. Ready            → Bean pronto para uso
...
8. Destruction      → @PreDestroy ou destroy-method
9. Shutdown         → Bean removido do contexto
```

### Escopos de Bean

```java
// SINGLETON (padrão) - Uma instância por aplicação
@Service
@Scope(scopeName = "singleton")
public class ResponsavelServiceImpl { }

// PROTOTYPE - Nova instância a cada injeção
@Service
@Scope(scopeName = "prototype")
public class TemporaryService { }

// REQUEST - Uma instância por requisição HTTP
@Service
@Scope(scopeName = "request")
public class RequestScopedService { }

// SESSION - Uma instância por sessão HTTP
@Service
@Scope(scopeName = "session")
public class SessionScopedService { }
```

---

## ✅ Validação de Dados

### Camadas de Validação

```
┌─────────────────────────────┐
│ 1. VALIDAÇÃO DO TIPO        │ (Automática pelo framework)
│    String → String OK       │ (int → String ❌ erro desserialização)
└─────────────────────────────┘

┌─────────────────────────────┐
│ 2. BEAN VALIDATION          │ (Anotações em DTOs)
│    @NotNull, @Email, @Size  │
│    Executada automaticamente │
└─────────────────────────────┘

┌─────────────────────────────┐
│ 3. VALIDAÇÃO DE NEGÓCIO     │ (Lógica em Services)
│    Email já existe?         │
│    Responsável está ativo?  │
└─────────────────────────────┘
```

### Exemplo Completo

```java
// DTO com Bean Validation
@Data
public class CriarResponsavelDTO {
    
    @NotNull(message = "Email não pode ser nulo")
    @Email(message = "Email deve corresponder ao formato valido: example@domain.com")
    @Size(min = 5, max = 100, message = "Email deve ter entre 5 e 100 caracteres")
    private String email;
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Nome deve conter apenas letras e espaços")
    @Size(min = 3, max = 150)
    private String nomeCompleto;
    
    @NotBlank
    @Size(min = 2, max = 50)
    private String nomeDoProjeto;
}

// Service com validação de negócio
@Service
public class ResponsavelServiceImpl implements ResponsavelService {
    
    private final ResponsavelRepository repository;
    
    @Override
    @Transactional
    public Responsavel criar(CriarResponsavelDTO dto) {
        
        // Validação 1: Email já existe?
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicatedResourceException(
                "Email " + dto.getEmail() + " já está registrado"
            );
        }
        
        // Validação 2: Projeto válido?
        if (!isProjetoValido(dto.getNomeDoProjeto())) {
            throw new ValidationException(
                "Projeto " + dto.getNomeDoProjeto() + " não existe"
            );
        }
        
        // Tudo OK, cria entidade
        Responsavel responsavel = Responsavel.builder()
            .email(dto.getEmail())
            .nomeCompleto(dto.getNomeCompleto())
            .nomeDoProjeto(dto.getNomeDoProjeto())
            .ativo(true)
            .build();
        
        return repository.save(responsavel);
    }
}

// Controller - Validação acontece automaticamente
@RestController
@RequestMapping("/responsaveis")
public class ResponsavelController {
    
    @PostMapping("/")
    // @Valid dispara Bean Validation
    // Se falhar: retorna 400 Bad Request
    public ResponseEntity<?> criar(@RequestBody @Valid CriarResponsavelDTO dto) {
        Responsavel responsavel = service.criar(dto);
        return ResponseEntity.status(201).body(responsavel);
    }
}
```

---

## 🚨 Tratamento de Exceções

### Estratégia Global com ExceptionHandler

```java
@RestControllerAdvice  // Intercepta exceções globalmente
@Slf4j
public class GlobalExceptionHandler {
    
    // 1. Validação falhou (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> erros = new HashMap<>();
        
        ex.getBindingResult()
            .getFieldErrors()
            .forEach(field -> 
                erros.put(
                    field.getField(),
                    field.getDefaultMessage()
                )
            );
        
        log.warn("Validação falhou: {}", erros);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validação falhou",
                erros
            ));
    }
    
    // 2. Recurso não encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
            ));
    }
    
    // 3. Validação de negócio falhou
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationError(ValidationException ex) {
        log.warn("Erro de negócio: {}", ex.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErroResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                null
            ));
    }
    
    // 4. Erro genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericError(Exception ex) {
        log.error("Erro inesperado", ex);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                null
            ));
    }
}

// DTO de resposta de erro
@Data
@AllArgsConstructor
public class ErroResponse {
    private int status;
    private String mensagem;
    private Map<String, String> detalhes;
}
```

### Exceções Customizadas

```java
// Base para exceções de negócio
public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// Recurso não encontrado
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Validação de negócio
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }
}

// Recurso duplicado
public class DuplicatedResourceException extends BusinessException {
    public DuplicatedResourceException(String message) {
        super(message);
    }
}
```

---

## 💾 Transações de Banco

### Gerenciamento de Transações

```java
@Service
public class PedidoDeCompraServiceImpl implements PedidoDeCompraService {
    
    private final PedidoDeCompraRepository pedidoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ResponsavelRepository responsavelRepository;
    
    // Transação: Tudo ou nada
    @Transactional
    public PedidoDeCompra criar(CriarPedidoDTO dto) {
        
        // 1. Busca fornecedor
        Fornecedor fornecedor = fornecedorRepository
            .findByCnpjOuCpf(dto.getCnpjOuCpf())
            .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
        
        // 2. Busca responsável
        Responsavel responsavel = responsavelRepository
            .findByEmail(dto.getEmailResponsavel())
            .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));
        
        // 3. Cria pedido
        PedidoDeCompra pedido = new PedidoDeCompra();
        pedido.setFornecedor(fornecedor);
        pedido.setResponsavel(responsavel);
        pedido.setValorAproximado(dto.getValorAproximado());
        
        // Se algum erro ocorrer aqui, toda a transação é revertida
        // (ROLLBACK automático)
        PedidoDeCompra saved = pedidoRepository.save(pedido);
        
        // Se saiu daqui com sucesso, transação é commitada
        return saved;
    }
    
    // Transação somente leitura (otimização)
    @Transactional(readOnly = true)
    public List<PedidoDeCompra> listarPendentes() {
        return pedidoRepository.findByCancelado(false);
    }
    
    // Propagação de transação
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarLog(String mensagem) {
        // Cria uma NOVA transação, independente da atual
        // Útil para logs que não devem afetar a operação principal
    }
}
```

### Niveis de Isolamento

```
1. READ_UNCOMMITTED (Não recomendado)
   - Dirty reads: lê dados não commitados
   
2. READ_COMMITTED (Padrão MySQL)
   - Evita dirty reads
   - Permite non-repeatable reads
   
3. REPEATABLE_READ (Padrão MySQL)
   - Evita dirty reads e non-repeatable reads
   - Permite phantom reads
   
4. SERIALIZABLE (Mais rigoroso)
   - Evita todos os problemas acima
   - Menor performance
```

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public Responsavel buscar(String email) { }

@Transactional(isolation = Isolation.REPEATABLE_READ)
public void criar(ResponsavelDTO dto) { }
```

---

## 📧 Integração com E-mail

### Configuração

```properties
# application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:seu-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:sua-senha}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### Service de E-mail

```java
@Service
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Autowired
    public EmailService(
        JavaMailSender mailSender,
        TemplateEngine templateEngine
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }
    
    public void enviarCobranca(NotaFiscal nota) throws MessagingException {
        
        try {
            // 1. Cria contexto Thymeleaf
            Context context = new Context();
            context.setVariable("nota", nota);
            context.setVariable("fornecedor", nota.getFornecedor());
            context.setVariable("dataVencimento", nota.getDataDeEmissao());
            
            // 2. Processa template HTML
            String html = templateEngine.process("cobranca", context);
            
            // 3. Cria mensagem MIME
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 4. Configura dados
            helper.setTo(nota.getResponsavel().getEmail());
            helper.setFrom("noreply@zupayments.com");
            helper.setSubject("Cobrança: Nota Fiscal #" + nota.getId());
            helper.setText(html, true);  // true = HTML
            
            // 5. Envia
            mailSender.send(message);
            
            log.info("Email enviado com sucesso para: {}", 
                nota.getResponsavel().getEmail());
            
        } catch (Exception ex) {
            log.error("Erro ao enviar email", ex);
            throw new EmailException("Falha ao enviar e-mail de cobrança", ex);
        }
    }
}
```

### Template Thymeleaf

```html
<!-- templates/cobranca.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Cobrança - Nota Fiscal</title>
</head>
<body>
    <h2>Cobrança de Nota Fiscal</h2>
    
    <p>Prezado(a) <strong th:text="${nota.responsavel.nomeCompleto}"></strong>,</p>
    
    <p>A seguinte nota fiscal está pendente de pagamento:</p>
    
    <table border="1">
        <tr>
            <td>Número da Nota:</td>
            <td th:text="${nota.numeroDaNota}"></td>
        </tr>
        <tr>
            <td>Fornecedor:</td>
            <td th:text="${nota.fornecedor.razaoSocial}"></td>
        </tr>
        <tr>
            <td>Valor:</td>
            <td th:text="${#numbers.formatCurrency(nota.valorAPagar)}"></td>
        </tr>
        <tr>
            <td>Data de Emissão:</td>
            <td th:text="${#dates.format(nota.dataDeEmissao, 'dd/MM/yyyy')}"></td>
        </tr>
    </table>
    
    <p>Por favor, procure regularizar este pagamento em sua maior brevidade.</p>
    
    <p>Atenciosamente,<br>
    <strong>Sistema ZUPayments</strong></p>
</body>
</html>
```

---

## 🔐 Autenticação JWT

### Fluxo JWT

```
1. CLIENT
   └─ POST /api/login
      ├─ email: "user@zup.com"
      └─ senha: "123456"

2. SERVER
   ├─ Busca usuário no BD
   ├─ Valida senha (BCrypt)
   └─ Gera token JWT assinado

3. Token JWT
   ├─ Header: { "alg": "HS512", "typ": "JWT" }
   ├─ Payload: { 
   │    "sub": "user@zup.com",
   │    "roles": ["ROLE_USER"],
   │    "iat": 1624665600,
   │    "exp": 1624752000
   │  }
   └─ Signature: HMACSHA512(header.payload, SECRET_KEY)

4. TOKEN ENVIADO
   └─ Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWI...

5. PRÓXIMAS REQUISIÇÕES
   ├─ Cliente inclui token no header
   ├─ Servidor valida assinatura
   └─ Se válido: permite acesso
```

### Implementação

```java
// 1. Configuration
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Hashing de senha
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}

// 2. JWT Token Provider
@Component
@Slf4j
public class TokenProvider {
    
    @Value("${jwt.secret:minha-chave-secreta-super-segura-12345}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")  // 24 horas
    private int jwtExpirationMs;
    
    public String generateToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getEmailFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error("Token inválido", ex);
            return false;
        }
    }
}

// 3. Filter JWT
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private TokenProvider tokenProvider;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                String email = tokenProvider.getEmailFromToken(token);
                
                // Cria autenticação
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            log.error("Erro ao processar JWT", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

// 4. Usar em Services/Controllers
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
    Usuario usuario = usuarioRepository
        .findByEmail(dto.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    
    if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
        throw new ValidationException("Senha inválida");
    }
    
    String token = tokenProvider.generateToken(usuario.getEmail());
    
    return ResponseEntity.ok(new TokenResponse(token));
}
```

---

**Versão:** 1.0.0  
**Última Atualização:** 18 de Abril de 2026

