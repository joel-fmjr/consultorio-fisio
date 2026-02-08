# Guia de Estudo: Consultório Fisio API

## OBJETIVO

Este guia te prepara para responder QUALQUER pergunta do professor sobre o projeto. Cada seção explica o "porquê" e o "como" de cada decisão, tecnologia e padrão usado.

---

## 1. VISAO GERAL DO PROJETO

**O que e:** Uma API REST para gerenciamento de consultorio de fisioterapia.

**Problema que resolve:** Fisioterapeutas autonomos usam cadernos/planilhas. Este sistema centraliza:
- Cadastro de pacientes
- Agendamento de consultas
- Fichas de avaliacao fisioterapeutica
- Evolucoes clinicas (acompanhamento do tratamento)
- Pagamentos via PIX (Mercado Pago)

**Arquitetura:** Camadas (Layered Architecture)
```
Cliente (Postman/Frontend)
    ↓ HTTP Request
Controller (recebe request, valida, delega)
    ↓
Service (regras de negocio)
    ↓
Repository (acesso ao banco)
    ↓
Database (PostgreSQL)
```

**Por que camadas?** Separacao de responsabilidades. Cada camada tem uma funcao unica:
- Controller: NAO tem logica de negocio, so roteia
- Service: TODA logica de negocio fica aqui
- Repository: APENAS operacoes de banco

---

## 2. TECNOLOGIAS E DEPENDENCIAS (pom.xml)

### 2.1 Spring Boot 3.5.8
**O que e:** Framework Java que simplifica a criacao de aplicacoes. Ele faz "auto-configuration" — detecta as dependencias e configura automaticamente.

**Por que usar:** Sem Spring Boot, voce teria que configurar manualmente servidor web, conexao com banco, serialização JSON, etc. O Spring Boot faz tudo isso automaticamente.

**Versao:** 3.5.8 (usa Spring Framework 6.x por baixo)

### 2.2 Java 21
**O que e:** Versao LTS (Long Term Support) mais recente do Java.
**No pom.xml:** `<java.version>21</java.version>`

### 2.3 spring-boot-starter-web
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**O que traz:**
- Tomcat embutido (servidor web — NAO precisa instalar separado)
- Spring MVC (framework para criar endpoints REST)
- Jackson (converte Java objects ↔ JSON automaticamente)

**Se o professor perguntar:** "O Tomcat vem embutido no starter-web. Quando a app sobe, o Spring Boot inicia o Tomcat automaticamente na porta configurada (8000)."

### 2.4 spring-boot-starter-data-jpa
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
**O que e JPA:** Java Persistence API — especificacao para mapeamento objeto-relacional (ORM).
**O que e Hibernate:** A implementacao do JPA usada pelo Spring Boot.
**O que faz:** Mapeia classes Java para tabelas do banco. Voce nao escreve SQL manual — o Hibernate gera.

**Se o professor perguntar:** "JPA e a especificacao (interface), Hibernate e a implementacao (classe concreta). O Spring Data JPA adiciona uma camada por cima que gera as queries automaticamente a partir dos nomes dos metodos do Repository."

### 2.5 spring-boot-starter-validation
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
**O que faz:** Permite usar anotacoes como `@NotBlank`, `@Email`, `@Size`, `@Min`, `@Max` nos DTOs para validar dados de entrada automaticamente.

**Como funciona:** Quando o controller recebe um `@Valid @RequestBody`, o Spring automaticamente valida o objeto. Se falhar, lanca `MethodArgumentNotValidException`.

### 2.6 PostgreSQL Driver
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```
**O que e:** Driver JDBC para o PostgreSQL. Permite que o Java se conecte ao banco PostgreSQL.
**scope=runtime:** So e necessario em tempo de execucao (nao em compilacao).

### 2.7 H2 Database
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
**O que e:** Banco de dados em memoria. Usado APENAS nos testes.
**Por que:** Testes nao devem depender de banco externo. O H2 sobe em memoria, roda os testes, e morre.

### 2.8 Lombok
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
**O que faz:** Gera codigo boilerplate em tempo de compilacao via anotacoes.

**Anotacoes usadas no projeto:**
| Anotacao | O que gera |
|----------|------------|
| `@Data` | getters, setters, toString, equals, hashCode |
| `@Builder` | Pattern Builder (cria objetos com `.builder().campo(valor).build()`) |
| `@NoArgsConstructor` | Construtor vazio (exigido pelo JPA) |
| `@AllArgsConstructor` | Construtor com todos os campos |
| `@RequiredArgsConstructor` | Construtor com campos `final` (usado para injecao de dependencia) |
| `@Slf4j` | Cria um logger `log` automaticamente |
| `@Builder.Default` | Define valor padrao no builder (ex: `isPaid = false`) |

**Se o professor perguntar:** "Lombok e um annotation processor. Ele gera o codigo em tempo de compilacao — se voce descompilar o .class, vai ver todos os getters/setters la. O pom.xml configura o `maven-compiler-plugin` com `annotationProcessorPaths` para o Lombok funcionar."

### 2.9 SpringDoc OpenAPI (Swagger)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.5</version>
</dependency>
```
**O que faz:** Gera documentacao interativa da API automaticamente em `/swagger-ui.html`.
**Como funciona:** Analisa os controllers, anotacoes como `@Tag`, `@Operation`, e os DTOs para gerar a documentacao.

### 2.10 Mercado Pago SDK
```xml
<dependency>
    <groupId>com.mercadopago</groupId>
    <artifactId>sdk-java</artifactId>
    <version>2.1.21</version>
</dependency>
```
**O que faz:** SDK oficial do Mercado Pago para Java. Permite criar pagamentos PIX, consultar status, etc.
**Classes usadas:** `PaymentClient`, `PaymentCreateRequest`, `PaymentPayerRequest`, `MPRequestOptions`

### 2.11 Spring Dotenv
```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```
**O que faz:** Carrega variaveis de ambiente do arquivo `.env` automaticamente, sem precisar exportar manualmente.

### 2.12 Spring Boot DevTools
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```
**O que faz:** Hot reload — quando voce muda o codigo, a aplicacao reinicia automaticamente sem voce parar/startar manualmente.

---

## 3. INJECAO DE DEPENDENCIA (Pergunta MUITO provavel)

### O que e?
Em vez de a classe CRIAR suas dependencias (com `new`), o Spring INJETA elas automaticamente.

### Como funciona no projeto?

**Exemplo concreto — PatientService.java:**
```java
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;  // Spring injeta automaticamente
    private final PatientMapper mapper;           // Spring injeta automaticamente
}
```

**Passo a passo do que acontece:**
1. `@Service` diz ao Spring: "essa classe e um bean gerenciado por voce"
2. `@RequiredArgsConstructor` (Lombok) gera um construtor com os campos `final`:
   ```java
   public PatientService(PatientRepository repository, PatientMapper mapper) {
       this.repository = repository;
       this.mapper = mapper;
   }
   ```
3. Quando o Spring cria o `PatientService`, ele ve que precisa de `PatientRepository` e `PatientMapper`
4. O Spring procura beans desses tipos no container e injeta automaticamente

### Anotacoes que criam beans:
| Anotacao | Uso | Arquivo exemplo |
|----------|-----|-----------------|
| `@Service` | Classes de logica de negocio | `PatientService.java` |
| `@Component` | Classes utilitarias gerais | `PatientMapper.java` |
| `@RestController` | Controllers REST | `PatientController.java` |
| `@Configuration` | Classes de configuracao | `MercadoPagoConfiguration.java` |
| `@Repository` | Repositorios (Spring Data cria a implementacao automaticamente) | `PatientRepository.java` (interface!) |

### Por que usar injecao de dependencia?
1. **Testabilidade:** Nos testes, voce pode injetar mocks em vez de dependencias reais
2. **Desacoplamento:** A classe nao sabe como criar suas dependencias
3. **Gerenciamento de ciclo de vida:** O Spring controla quando criar e destruir os beans

### No teste (PatientServiceTest.java):
```java
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository repository;    // Mock, nao real
    @Mock
    private PatientMapper mapper;            // Mock, nao real
    @InjectMocks
    private PatientService service;          // Injeta os mocks acima
}
```

---

## 4. ENTIDADES JPA (MAPEAMENTO OBJETO-RELACIONAL)

### 4.1 Anotacoes de entidade explicadas

**Patient.java — Exemplo completo:**
```java
@Entity                              // Diz ao JPA: "essa classe mapeia uma tabela"
@Table(name = "patients")           // Nome da tabela no banco
@Data                                // Lombok: getters, setters, toString, equals, hashCode
@Builder                             // Lombok: pattern builder
@NoArgsConstructor                   // JPA EXIGE construtor vazio
@AllArgsConstructor                  // Para o @Builder funcionar
public class Patient {

    @Id                              // Chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment do banco
    private Long id;

    @Column(nullable = false)        // NOT NULL no banco
    private String name;

    @Column(unique = true)           // UNIQUE constraint
    private String cpf;

    @Column(name = "birth_date")     // Nome da coluna diferente do campo Java
    private LocalDate birthDate;     // Java: camelCase → Banco: snake_case

    @Column(name = "medical_history", columnDefinition = "TEXT")  // Tipo TEXT (sem limite)
    private String medicalHistory;
}
```

**Se o professor perguntar sobre GenerationType.IDENTITY:**
"IDENTITY usa o auto-increment nativo do banco (SERIAL no PostgreSQL). A alternativa seria SEQUENCE (que cria uma sequence no banco) ou AUTO (que deixa o Hibernate decidir)."

### 4.2 Relacionamentos entre entidades

**Appointment.java:**
```java
@ManyToOne(fetch = FetchType.LAZY)           // Muitas consultas → 1 paciente
@JoinColumn(name = "patient_id", nullable = false)  // Coluna FK no banco
private Patient patient;
```

**O que e FetchType.LAZY?**
- LAZY: Carrega o paciente SO quando voce acessar `appointment.getPatient()` (query separada)
- EAGER: Carrega o paciente SEMPRE que carregar a consulta (JOIN automatico)
- **Por que LAZY?** Performance. Se voce lista 100 consultas, nao quer carregar 100 pacientes automaticamente.

**Se o professor perguntar:** "Todas as relacoes @ManyToOne do projeto usam FetchType.LAZY para evitar o problema do N+1 queries."

### 4.3 Lifecycle Callbacks (@PrePersist e @PreUpdate)

```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDate.now();
    updatedAt = LocalDate.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDate.now();
}
```

**O que fazem:** O JPA chama esses metodos automaticamente:
- `@PrePersist`: Antes de inserir no banco (INSERT)
- `@PreUpdate`: Antes de atualizar no banco (UPDATE)

**No Appointment.java tem logica extra:**
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    calculateEndTime();  // Calcula endTime baseado em startTime + duration
}
```

### 4.4 Enums no JPA

```java
@Enumerated(EnumType.STRING)   // Salva o NOME do enum no banco ("ONE_HOUR")
@Column(nullable = false, length = 20)
private AppointmentDuration duration;
```

**EnumType.STRING vs ORDINAL:**
- STRING: Salva "ONE_HOUR" → legivel, seguro contra reordenacao
- ORDINAL: Salva 0, 1, 2 → se adicionar um enum no meio, quebra tudo

---

## 5. REPOSITORIES (Acesso ao Banco)

### 5.1 Como funciona Spring Data JPA

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByCpf(String cpf);
    Optional<Patient> findByEmail(String email);
    List<Patient> findByNameContainingIgnoreCase(String name);
}
```

**MAGIA:** Voce NAO implementa essa interface! O Spring Data JPA gera a implementacao em runtime baseada no nome do metodo.

**Decomposicao dos nomes de metodos:**
| Metodo | Query gerada |
|--------|-------------|
| `findByCpf(String cpf)` | `SELECT * FROM patients WHERE cpf = ?` |
| `findByEmail(String email)` | `SELECT * FROM patients WHERE email = ?` |
| `findByNameContainingIgnoreCase(String name)` | `SELECT * FROM patients WHERE LOWER(name) LIKE LOWER('%name%')` |
| `findByPatientIdOrderByEvolutionNumberAsc(Long id)` | `SELECT * FROM evolutions WHERE patient_id = ? ORDER BY evolution_number ASC` |
| `countByPatientId(Long patientId)` | `SELECT COUNT(*) FROM evolutions WHERE patient_id = ?` |
| `findTopByPatientIdOrderByEvolutionNumberDesc(Long id)` | `SELECT * FROM evolutions WHERE patient_id = ? ORDER BY evolution_number DESC LIMIT 1` |

**JpaRepository<Patient, Long>:**
- `Patient` = tipo da entidade
- `Long` = tipo da chave primaria

**Metodos herdados do JpaRepository (nao precisa declarar):**
- `findAll()`, `findById(id)`, `save(entity)`, `deleteById(id)`, `existsById(id)`, `count()`

### 5.2 Optional como retorno
```java
Optional<Patient> findByCpf(String cpf);
```
`Optional` representa um valor que pode ou nao existir. Evita `NullPointerException`:
```java
Patient patient = repository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
```

---

## 6. DTOs (Data Transfer Objects)

### 6.1 Por que usar DTOs?
1. **Seguranca:** NAO expor a entidade diretamente (pode ter campos sensiveis)
2. **Controle:** Request e Response podem ter campos diferentes
3. **Validacao:** Anotacoes de validacao ficam no DTO, nao na entidade

### 6.2 Exemplo — PatientRequestDTO
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {

    @NotBlank(message = "Name is required")          // Nao pode ser null, vazio, ou so espacos
    @Size(min = 3, max = 100, message = "...")       // Tamanho minimo/maximo
    private String name;

    @Pattern(regexp = "\\d{11}", message = "...")    // Regex: exatamente 11 digitos
    private String cpf;

    @Email(message = "Email must be valid")           // Valida formato de email
    private String email;

    @NotBlank                                         // Obrigatorio
    @Pattern(regexp = "\\d{10,11}", message = "...")  // 10 ou 11 digitos
    private String phone;

    @Past(message = "Birth date must be in the past")  // Data deve ser no passado
    private LocalDate birthDate;
}
```

### 6.3 Validacoes usadas no projeto
| Anotacao | Regra | Exemplo |
|----------|-------|---------|
| `@NotBlank` | Nao null, nao vazio, nao so espacos | `name` |
| `@NotNull` | Nao null (pode ser vazio) | `patientId`, `startTime` |
| `@Email` | Formato de email valido | `email` |
| `@Pattern` | Regex customizado | `cpf` (11 digitos) |
| `@Size` | Tamanho min/max de string | `name` (3-100) |
| `@Past` | Data no passado | `birthDate` |
| `@PastOrPresent` | Data no passado ou hoje | `assessmentDate` |
| `@Future` | Data no futuro | `startTime` do appointment |
| `@Min` / `@Max` | Valor numerico min/max | `painScale` (0-10) |
| `@DecimalMin` / `@DecimalMax` | Valor decimal min/max | `amount` (0.01+) |

---

## 7. CONTROLLERS (Endpoints REST)

### 7.1 Anotacoes explicadas

```java
@RestController                               // = @Controller + @ResponseBody
@RequestMapping("/api/patients")              // Prefixo de URL para todos endpoints
@RequiredArgsConstructor                      // Injecao de dependencia via construtor
@Tag(name = "Patients", description = "...")  // Swagger: agrupa endpoints
public class PatientController {

    private final PatientService service;     // Injetado pelo Spring

    @GetMapping                               // HTTP GET em /api/patients
    @Operation(summary = "...", description = "...")  // Swagger: descricao do endpoint
    public ResponseEntity<List<PatientResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());   // HTTP 200 + body
    }

    @GetMapping("/{id}")                      // HTTP GET em /api/patients/{id}
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable Long id) {
        // @PathVariable: extrai {id} da URL
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping                              // HTTP POST em /api/patients
    public ResponseEntity<PatientResponseDTO> create(
            @Valid @RequestBody PatientRequestDTO dto) {
        // @Valid: ativa validacao do DTO
        // @RequestBody: converte JSON do body para objeto Java (Jackson faz)
        PatientResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // HTTP 201
    }

    @DeleteMapping("/{id}")                   // HTTP DELETE em /api/patients/{id}
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();  // HTTP 204 (sem body)
    }
}
```

### 7.2 Metodos HTTP usados
| Metodo | Uso | Codigo de resposta |
|--------|-----|-------------------|
| GET | Buscar dados | 200 OK |
| POST | Criar recurso | 201 CREATED |
| PUT | Atualizar recurso completo | 200 OK |
| PATCH | Atualizar parcialmente | 200 OK (usado em markAsPaid, cancelAppointment) |
| DELETE | Remover recurso | 204 NO CONTENT |

### 7.3 ResponseEntity
`ResponseEntity` permite controlar: status code, headers e body da resposta.
```java
ResponseEntity.ok(body)                        // 200 + body
ResponseEntity.status(HttpStatus.CREATED).body(body)  // 201 + body
ResponseEntity.noContent().build()             // 204 sem body
```

---

## 8. SERVICES (Regras de Negocio)

### 8.1 Padrao geral
```java
@Service                        // Bean gerenciado pelo Spring
@RequiredArgsConstructor        // Injecao via construtor
public class PatientService {
    private final PatientRepository repository;
    private final PatientMapper mapper;
}
```

### 8.2 @Transactional
```java
@Transactional
public PatientResponseDTO create(PatientRequestDTO dto) { ... }
```
**O que faz:** Garante que todas as operacoes de banco dentro do metodo sejam atomicas:
- Se tudo der certo → COMMIT
- Se der excecao → ROLLBACK automatico

**Quando usar:** Em metodos que modificam dados (create, update, delete).
**Quando NAO usar:** Em metodos de leitura (findAll, findById) — nao precisa.

### 8.3 Regras de negocio implementadas

**Patient:**
- CPF deve ser unico (verifica antes de salvar)
- Email deve ser unico (verifica antes de salvar)
- No update, verifica se CPF/Email mudou antes de validar unicidade

**Appointment:**
- Paciente deve existir antes de criar consulta
- `endTime` e calculado automaticamente: `startTime + duration.getMinutes()`
- `markAsPaid()` muda `isPaid = true`
- `cancelAppointment()` muda `isCancelled = true`

**Evolution:**
- Numero da evolucao e auto-incrementado por paciente: `countByPatientId + 1`
- NAO pode trocar o paciente no update (throw IllegalArgumentException)
- `evolutionDate` padrao e a data/hora atual se nao informada

**Payment (PIX):**
- Gera referencia externa unica: `"FISIO-" + UUID`
- Cria chave de idempotencia para evitar pagamentos duplicados
- Calcula data de expiracao (padrao 24h)
- Chama API do Mercado Pago, extrai QR code do response
- Salva tudo no banco

---

## 9. MAPPERS (Conversao Entity ↔ DTO)

### 9.1 O que fazem?
Convertem entre entidades JPA e DTOs. Sao classes `@Component` com metodos manuais.

```java
@Component
public class PatientMapper {
    public PatientResponseDTO toResponseDTO(Patient entity) {
        return PatientResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                // ... todos os campos
                .build();
    }

    public Patient toEntity(PatientRequestDTO dto) {
        return Patient.builder()
                .name(dto.getName())
                // ... (sem id, createdAt, updatedAt — sao gerados)
                .build();
    }

    public List<PatientResponseDTO> toResponseDTOList(List<Patient> entities) {
        return entities.stream()
                .map(this::toResponseDTO)     // method reference
                .collect(Collectors.toList());
    }
}
```

### 9.2 Por que nao usar MapStruct?
O projeto usa mapeamento manual. MapStruct seria uma alternativa que gera mapeamentos em compilacao. O manual e mais simples para projetos pequenos.

---

## 10. TRATAMENTO DE EXCECOES

### 10.1 @ControllerAdvice (GlobalExceptionHandler.java)
```java
@ControllerAdvice    // Intercepta excecoes de TODOS os controllers
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)   // Captura essa excecao
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());  // 404
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
```

### 10.2 Excecoes customizadas
| Excecao | Quando e lancada | HTTP Status |
|---------|------------------|-------------|
| `ResourceNotFoundException` | Entidade nao encontrada no banco | 404 |
| `PaymentProcessingException` | Erro ao processar pagamento MP | 400 |
| `IllegalArgumentException` | CPF/Email duplicado, troca de paciente | 400 |
| `MethodArgumentNotValidException` | Validacao do DTO falhou (@Valid) | 400 |
| `DataIntegrityViolationException` | Violacao de constraint no banco | 409 |

### 10.3 Fluxo de tratamento
```
Controller recebe request → @Valid falha → MethodArgumentNotValidException
    ↓ (se passar)
Service executa logica → lanca ResourceNotFoundException
    ↓
@ControllerAdvice captura → retorna JSON padronizado com status correto
```

---

## 11. PAGAMENTOS (Integracao Mercado Pago)

### 11.1 Fluxo completo de pagamento PIX

```
1. Frontend chama POST /api/payments/pix com {patientId, amount}
    ↓
2. PaymentController recebe, valida, chama PaymentService
    ↓
3. PaymentService:
   a. Valida paciente existe
   b. Valida appointment existe (se fornecido)
   c. Gera externalReference: "FISIO-{UUID}"
   d. Cria idempotency key (evita cobrar 2x)
   e. Calcula expiracao (padrao 24h)
   f. Monta PaymentCreateRequest do Mercado Pago
   g. Chama client.create() → API do MP
   h. Extrai qrCode, qrCodeBase64, ticketUrl do response
   i. Salva Payment no banco com status PENDING
   j. Retorna PIXPaymentResponseDTO
    ↓
4. Frontend mostra QR code para o paciente pagar
    ↓
5. Paciente paga via app do banco
    ↓
6. Mercado Pago envia webhook para POST /api/webhooks/mercadopago
    ↓
7. WebhookController:
   a. Extrai paymentId do payload
   b. Chama paymentService.processWebhookNotification(paymentId)
    ↓
8. PaymentService.processWebhookNotification:
   a. Consulta status atualizado na API do MP (client.get(paymentId))
   b. Atualiza status no banco (PENDING → APPROVED)
   c. Salva
```

### 11.2 Configuracao do Mercado Pago
```java
@Configuration
@Slf4j
public class MercadoPagoConfiguration {
    @Value("${mercadopago.access-token}")    // Le do application.properties
    private String accessToken;

    @PostConstruct                            // Executa apos o bean ser criado
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);  // Configura SDK
    }
}
```

`@PostConstruct`: Metodo executado DEPOIS que o Spring instanciou e injetou tudo. Ideal para inicializacao.

### 11.3 Webhook — por que retorna sempre 200?
```java
} catch (Exception e) {
    log.error("Error processing webhook: {}", e.getMessage(), e);
    return ResponseEntity.ok().build();  // Retorna 200 MESMO com erro
}
```
**Motivo:** Se retornar erro, o Mercado Pago reenvia o webhook repetidamente. Retornando 200, ele para de reenviar.

---

## 12. CONFIGURACAO (application.properties)

### 12.1 Profiles (Perfis)
```properties
# application.properties (base)
spring.profiles.active=dev
```

**3 perfis no projeto:**
| Perfil | Banco | DDL Auto | SQL visivel |
|--------|-------|----------|-------------|
| dev | PostgreSQL (localhost) | `update` (cria/altera tabelas) | Sim |
| prod | PostgreSQL | `validate` (so valida, nao altera) | Nao |
| test | H2 (memoria) | `create-drop` (cria, testa, destroi) | Nao |

**Se o professor perguntar sobre ddl-auto:**
- `update`: Compara entidades com banco e adiciona colunas/tabelas faltantes (NUNCA remove)
- `validate`: So verifica se o banco bate com as entidades (prod safe)
- `create-drop`: Cria tudo do zero, no final destroi (so para testes)

### 12.2 Variaves de ambiente
```properties
spring.datasource.username=${DB_USERNAME:postgres}
```
- `${DB_USERNAME:postgres}` → Le variavel de ambiente DB_USERNAME, se nao existir usa "postgres"
- O spring-dotenv carrega do arquivo `.env` automaticamente

---

## 13. DOCKER

### 13.1 Dockerfile (Multi-stage build)
```dockerfile
# Estagio 1: BUILD
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests     # Compila e gera o JAR

# Estagio 2: RUN
FROM eclipse-temurin:21-jre-alpine     # Imagem menor (so JRE, nao JDK)
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar    # Copia JAR do estagio anterior
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Por que multi-stage?** A imagem final so tem o JRE + JAR. O JDK e Maven ficam apenas no build. Imagem final fica ~200MB em vez de ~800MB.

### 13.2 docker-compose.yml
```yaml
services:
  postgres:                              # Servico do banco
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: consultorio_fisio
    ports:
      - "5432:5432"                      # Porta host:container
    volumes:
      - postgres_data:/var/lib/postgresql/data   # Dados persistentes
    healthcheck:                         # Verifica se o banco ta pronto
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME:-postgres}"]

  app:                                   # Servico da aplicacao
    build: .                             # Usa o Dockerfile local
    ports:
      - "8000:8000"
    depends_on:
      postgres:
        condition: service_healthy       # So inicia depois do banco estar HEALTHY
```

**Se o professor perguntar sobre depends_on:**
"`depends_on` com `condition: service_healthy` garante que a app so sobe DEPOIS que o PostgreSQL estiver aceitando conexoes. Sem isso, a app poderia tentar conectar antes do banco estar pronto."

---

## 14. TESTES

### 14.1 Tipos de teste no projeto

**Testes unitarios (Service):** Usam Mockito para simular dependencias.
```java
@ExtendWith(MockitoExtension.class)     // Ativa Mockito
class PatientServiceTest {
    @Mock
    private PatientRepository repository;   // Simula o repositorio
    @Mock
    private PatientMapper mapper;           // Simula o mapper
    @InjectMocks
    private PatientService service;         // Classe sendo testada (com mocks injetados)
}
```

**Testes de integracao (Controller):** Sobem contexto Spring e testam endpoint completo.
```java
@SpringBootTest                           // Sobe contexto Spring completo
@AutoConfigureMockMvc                     // Configura MockMvc
@ActiveProfiles("test")                   // Usa application-test.properties (H2)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;              // Simula chamadas HTTP
}
```

### 14.2 Mockito — como funciona
```java
// Configura: quando chamar repository.findById(1L), retorna Optional com patient
when(repository.findById(1L)).thenReturn(Optional.of(patient));

// Executa o metodo sendo testado
PatientResponseDTO result = service.findById(1L);

// Verifica
assertNotNull(result);
assertEquals("Joao Silva", result.getName());
verify(repository).findAll();              // Verifica que findAll foi chamado
verify(repository, never()).save(any());   // Verifica que save NAO foi chamado
```

### 14.3 assertThrows — testando excecoes
```java
@Test
void shouldThrowExceptionWhenPatientNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
}
```

---

## 15. ENUMS DO PROJETO

### AppointmentDuration
```java
public enum AppointmentDuration {
    ONE_HOUR("1h", 60),
    ONE_HOUR_THIRTY("1h30", 90),
    TWO_HOURS("2h", 120);

    private final String displayName;
    private final int minutes;
}
```

### PaymentStatus
```java
public enum PaymentStatus {
    PENDING, APPROVED, AUTHORIZED, IN_PROCESS,
    IN_MEDIATION, REJECTED, CANCELLED, REFUNDED, CHARGED_BACK
}
```

### EatingHabits
```java
public enum EatingHabits {
    HEALTHY("Saudavel"), MODERATE("Moderado"), POOR("Ruim");
}
```

---

## 16. PERGUNTAS FREQUENTES DO PROFESSOR

### "Explique a arquitetura do projeto"
"O projeto usa arquitetura em camadas: Controller recebe a requisicao HTTP, delega para o Service que contem a logica de negocio, que usa o Repository para acessar o banco. DTOs separam a representacao da API da entidade do banco. Exceptions sao tratadas globalmente pelo @ControllerAdvice."

### "O que e Spring Data JPA?"
"E uma camada de abstracao sobre o JPA/Hibernate. Voce cria uma interface Repository e o Spring gera a implementacao automaticamente. Os metodos de query sao derivados dos nomes — por exemplo, `findByNameContainingIgnoreCase` gera um SELECT com LIKE case-insensitive."

### "Como funciona a injecao de dependencia?"
"O Spring gerencia os beans (objetos) no seu container IoC. Classes marcadas com @Service, @Component, @RestController sao beans. O @RequiredArgsConstructor do Lombok gera um construtor com os campos final, e o Spring injeta os beans correspondentes automaticamente."

### "O que e o @Transactional?"
"Garante atomicidade. Se qualquer operacao dentro do metodo falhar, todas sao revertidas (rollback). E implementado via proxy — o Spring envolve a classe em um proxy que abre/fecha transacoes."

### "Por que usar DTOs?"
"Tres motivos: seguranca (nao expor a entidade), controle (Request e Response podem ter campos diferentes), e validacao (anotacoes como @NotBlank ficam no DTO de request)."

### "Como funciona o pagamento PIX?"
"O sistema cria um PaymentCreateRequest com os dados do pagamento, chama a API do Mercado Pago via SDK, recebe o QR code PIX, salva no banco. Quando o paciente paga, o Mercado Pago envia um webhook para nosso endpoint que atualiza o status do pagamento."

### "O que e @ControllerAdvice?"
"E um interceptador global de excecoes. Em vez de cada controller tratar suas proprias excecoes, o @ControllerAdvice captura excecoes de todos os controllers e retorna respostas padronizadas em JSON."

### "Explique o Dockerfile"
"Usa multi-stage build. Primeiro estagio compila com Maven e JDK. Segundo estagio copia so o JAR para uma imagem menor com apenas JRE. A imagem final e leve — nao tem o codigo fonte nem o Maven."

### "O que e o @Builder?"
"E do Lombok. Implementa o Design Pattern Builder, que permite criar objetos de forma fluente: `Patient.builder().name('Joao').phone('11999').build()`. E mais legivel que construtores com muitos parametros."

### "Por que H2 nos testes?"
"O H2 e um banco em memoria — sobe instantaneamente, nao precisa de instalacao, e destroido depois dos testes. Isso garante que os testes sejam rapidos, isolados e reproduziveis sem depender de infra externa."

### "O que sao os Mappers?"
"Sao classes que convertem entre entidades JPA e DTOs. O toEntity() converte o DTO de request para a entidade (para salvar no banco), e o toResponseDTO() converte a entidade para o DTO de response (para retornar na API)."

### "Como funciona a validacao?"
"O controller recebe o DTO com @Valid @RequestBody. O Spring valida automaticamente usando as anotacoes do DTO (@NotBlank, @Email, etc). Se falhar, lanca MethodArgumentNotValidException que e capturada pelo @ControllerAdvice e retorna um JSON com os erros de validacao."

---

## 17. ESTRUTURA DE PASTAS — MAPA MENTAL

```
consultorio-fisio/
├── src/main/java/com/joel/consultorio_fisio/
│   ├── ConsultorioFisioApplication.java    ← PONTO DE ENTRADA (@SpringBootApplication)
│   │
│   ├── configurations/
│   │   ├── MercadoPagoConfiguration.java   ← Configura SDK do Mercado Pago (@Configuration)
│   │   └── OpenApiConfig.java              ← Configura Swagger (@Configuration)
│   │
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java     ← Trata excecoes globalmente (@ControllerAdvice)
│   │   ├── ResourceNotFoundException.java  ← 404 customizado
│   │   └── PaymentProcessingException.java ← Erro de pagamento customizado
│   │
│   ├── patient/                            ← MODULO PACIENTE
│   │   ├── Patient.java                    ← Entidade JPA (@Entity)
│   │   ├── PatientRepository.java          ← Interface de acesso ao banco
│   │   ├── PatientService.java             ← Logica de negocio (@Service)
│   │   ├── PatientController.java          ← Endpoints REST (@RestController)
│   │   ├── PatientMapper.java              ← Conversao Entity↔DTO (@Component)
│   │   └── dtos/
│   │       ├── PatientRequestDTO.java      ← Dados de entrada (com validacoes)
│   │       └── PatientResponseDTO.java     ← Dados de saida
│   │
│   ├── appointment/                        ← MODULO CONSULTA (mesmo padrao)
│   │   ├── Appointment.java
│   │   ├── AppointmentDuration.java        ← Enum (1h, 1h30, 2h)
│   │   ├── AppointmentRepository.java
│   │   ├── AppointmentService.java
│   │   ├── AppointmentController.java
│   │   ├── AppointmentMapper.java
│   │   └── dtos/
│   │
│   ├── assessment/                         ← MODULO AVALIACAO (mesmo padrao)
│   │   ├── Assessment.java                 ← Entidade com 50+ campos medicos
│   │   ├── EatingHabits.java               ← Enum (HEALTHY, MODERATE, POOR)
│   │   └── ...
│   │
│   ├── evolution/                          ← MODULO EVOLUCAO (mesmo padrao)
│   │   └── ...
│   │
│   └── payment/                            ← MODULO PAGAMENTO
│       ├── Payment.java
│       ├── PaymentStatus.java              ← Enum (PENDING, APPROVED, REJECTED, ...)
│       ├── PaymentRepository.java
│       ├── PaymentService.java             ← Integracao com Mercado Pago
│       ├── PaymentController.java
│       ├── WebhookController.java          ← Recebe notificacoes do Mercado Pago
│       ├── PaymentMapper.java
│       └── dtos/
│           ├── PaymentRequestDTO.java
│           ├── PaymentResponseDTO.java
│           └── PIXPaymentResponseDTO.java  ← Response especifico para criacao de PIX
│
├── src/main/resources/
│   ├── application.properties              ← Config base
│   ├── application-dev.properties          ← Config desenvolvimento (PostgreSQL)
│   ├── application-prod.properties         ← Config producao
│   └── application-test.properties         ← Config testes (H2)
│
├── src/test/java/com/joel/consultorio_fisio/
│   ├── patient/
│   │   ├── PatientServiceTest.java         ← Testes unitarios com Mockito
│   │   └── PatientControllerTest.java      ← Testes de integracao com MockMvc
│   ├── appointment/
│   ├── assessment/
│   ├── evolution/
│   └── payment/
│
├── pom.xml                                 ← Dependencias Maven
├── Dockerfile                              ← Multi-stage build
├── docker-compose.yml                      ← PostgreSQL + App
└── .env                                    ← Variaveis de ambiente (NAO versionado)
```

---

## 18. FLUXO COMPLETO DE UMA REQUEST (EXEMPLO)

### POST /api/patients — Criar paciente

```
1. HTTP POST chega no Tomcat (embutido no Spring Boot)
    ↓
2. Spring MVC roteia para PatientController.create()
   (match: @PostMapping no @RequestMapping("/api/patients"))
    ↓
3. Jackson deserializa o JSON body → PatientRequestDTO
   (@RequestBody faz essa conversao)
    ↓
4. Bean Validation valida o DTO (@Valid ativa as anotacoes)
   - @NotBlank name? ✓
   - @Pattern cpf? ✓
   - @Email email? ✓
   Se falhar → MethodArgumentNotValidException → GlobalExceptionHandler → 400
    ↓
5. PatientController.create(dto) chama service.create(dto)
    ↓
6. PatientService.create(dto):
   a. Verifica se CPF ja existe → repository.findByCpf()
   b. Verifica se Email ja existe → repository.findByEmail()
   c. Converte DTO → Entity: mapper.toEntity(dto)
   d. Salva: repository.save(patient)
      → Hibernate gera: INSERT INTO patients (name, cpf, ...) VALUES (?, ?, ...)
      → @PrePersist seta createdAt e updatedAt
   e. Converte Entity → ResponseDTO: mapper.toResponseDTO(saved)
    ↓
7. Controller retorna ResponseEntity.status(201).body(responseDTO)
    ↓
8. Jackson serializa PatientResponseDTO → JSON
    ↓
9. Response HTTP 201 com JSON no body
```

---

## 19. GLOSSARIO RAPIDO

| Termo | Significado |
|-------|------------|
| **Bean** | Objeto gerenciado pelo Spring Container |
| **IoC** | Inversion of Control — o framework controla o ciclo de vida dos objetos |
| **DI** | Dependency Injection — o framework injeta as dependencias |
| **ORM** | Object-Relational Mapping — mapeia classes Java para tabelas |
| **JPA** | Java Persistence API — especificacao ORM |
| **Hibernate** | Implementacao do JPA |
| **DTO** | Data Transfer Object — objeto para transferir dados entre camadas |
| **CRUD** | Create, Read, Update, Delete |
| **REST** | Representational State Transfer — estilo arquitetural para APIs |
| **Webhook** | Callback HTTP — servico externo chama nosso endpoint para notificar |
| **Idempotencia** | Operacao pode ser repetida sem efeito colateral |
| **FK** | Foreign Key — chave estrangeira |
| **DDL** | Data Definition Language — comandos SQL de estrutura (CREATE TABLE, ALTER) |

---

## 20. DICAS PARA A APRESENTACAO

1. **Comece pela visao geral:** "E uma API REST para gestao de consultorio de fisioterapia, com 5 modulos: pacientes, consultas, avaliacoes, evolucoes e pagamentos PIX."

2. **Mostre o Swagger:** Rode a aplicacao e abra `localhost:8000/swagger-ui.html`. E visualmente impressionante e mostra que funciona.

3. **Demonstre o fluxo de pagamento:** E o diferencial do projeto. Mostre a criacao do PIX com QR code.

4. **Se perguntarem algo que nao sabe:** "Essa parte foi implementada seguindo a documentacao oficial do Spring Boot / Mercado Pago. Posso detalhar o fluxo geral..." e redirecione para algo que voce sabe.

5. **Prepare-se para estas perguntas-chave:**
   - "O que e injecao de dependencia?" (Secao 3)
   - "Explique a arquitetura" (Secao 1)
   - "Como funciona o JPA?" (Secao 4)
   - "Como funciona a validacao?" (Secao 6)
   - "Explique o Docker" (Secao 13)
