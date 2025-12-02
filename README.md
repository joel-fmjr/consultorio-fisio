# Consultório Fisio – API de Gestão para Profissionais de Fisioterapia

## 1. Visão Geral

O **Consultório Fisio** é uma API RESTful desenvolvida para auxiliar fisioterapeutas autônomos e pequenos consultórios na gestão completa de suas atividades diárias. O sistema abrange desde o cadastro de pacientes até o controle de pagamentos, passando por agendamento de consultas, registro de evoluções clínicas e avaliações fisioterapêuticas.



O projeto visa aplicar os conceitos de **Desenvolvimento de Sistemas Corporativos**, utilizando **Spring Boot**, arquitetura em camadas e boas práticas de engenharia de software.



## 2. Executando com Docker

### Pré-requisitos

- Docker
- Docker Compose



### Como Executar

1. **Crie o arquivo `.env` a partir do `.env.example`:**

```bash
cp .env.example .env
```

2. **Edite o arquivo `.env` e configure suas credenciais do Mercado Pago:**

```env
# Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Mercado Pago Configuration
MP_ACCESS_TOKEN=TEST-your-test-access-token-here
MP_WEBHOOK_SECRET=your-webhook-secret-here
```

> **Nota:** Obtenha seu token de acesso em: https://www.mercadopago.com.br/developers/panel/app

3. **Execute o Docker Compose:**

```bash
docker-compose up --build
```

Após a execução, a API estará disponível em `http://localhost:8000` e a documentação interativa (Swagger UI) em `http://localhost:8000/swagger-ui.html`.



Para parar todos os serviços, pressione `Ctrl+C` no terminal ou execute `docker-compose down`.



## 3. Justificativa do Tema

A escolha do tema "Gestão de Consultório de Fisioterapia" justifica-se pela resolução de problemas reais enfrentados por profissionais da área:

* **Gestão Manual de Pacientes:** Substituir cadernos e planilhas por um sistema centralizado.

* **Agendamento Desorganizado:** Evitar conflitos de horários e esquecimentos de consultas.

* **Histórico Clínico Disperso:** Centralizar informações de avaliações e evoluções clínicas.

* **Controle Financeiro Precário:** Facilitar o controle de pagamentos via PIX com Mercado Pago.



Além de resolver um problema real, este projeto serve como **trabalho acadêmico** para a disciplina de **Desenvolvimento de Sistemas Corporativos** do curso de Tecnologia em Análise e Desenvolvimento de Sistemas do IFRN.

**PS:** O motivo real é que minha namorada está cobrando esse sistema tem tempo kkk



## 4. Entidades e Relacionamentos

O sistema é fundamentado nas seguintes entidades principais:



* **Patient (Paciente):** Representa os pacientes do consultório.

    * *Atributos:* ID, Nome, CPF, Data de Nascimento, Telefone, Email, Endereço.

    * *Relacionamentos:* Possui muitos *Appointments*, *Assessments* e *Evolutions*.

* **Appointment (Consulta):** Representa os agendamentos de consultas.

    * *Atributos:* ID, Data/Hora, Duração, Status, Observações.

    * *Relacionamentos:* Pertence a um *Patient*.

* **Assessment (Avaliação Fisioterapêutica):** Registro completo de avaliação inicial do paciente.

    * *Atributos:* ID, Queixa Principal, História Clínica, Medicamentos, Hábitos Alimentares, Objetivos de Tratamento.

    * *Relacionamentos:* Pertence a um *Patient*.

* **Evolution (Evolução):** Registro de evolução clínica durante o tratamento.

    * *Atributos:* ID, Data, Descrição, Observações.

    * *Relacionamentos:* Pertence a um *Patient*.

* **Payment (Pagamento):** Controle de pagamentos via PIX integrado com Mercado Pago.

    * *Atributos:* ID, Valor, Status, QR Code, Payment ID (Mercado Pago), Descrição.

    * *Relacionamentos:* Pertence a um *Patient*.



## 5. Funcionalidades Principais (Endpoints)



### 5.1. Gerenciamento de Pacientes

* **POST** `/api/patients` - Criar novo paciente.

* **GET** `/api/patients` - Listar todos os pacientes.

* **GET** `/api/patients/{id}` - Obter paciente por ID.

* **PUT** `/api/patients/{id}` - Atualizar paciente.

* **DELETE** `/api/patients/{id}` - Excluir paciente.



### 5.2. Gerenciamento de Consultas

* **POST** `/api/appointments` - Criar nova consulta.

* **GET** `/api/appointments` - Listar todas as consultas.

* **GET** `/api/appointments/{id}` - Obter consulta por ID.

* **GET** `/api/appointments/patient/{patientId}` - Listar consultas de um paciente.

* **PUT** `/api/appointments/{id}` - Atualizar consulta.

* **DELETE** `/api/appointments/{id}` - Excluir consulta.



### 5.3. Gerenciamento de Avaliações

* **POST** `/api/assessments` - Criar nova avaliação fisioterapêutica.

* **GET** `/api/assessments` - Listar todas as avaliações.

* **GET** `/api/assessments/{id}` - Obter avaliação por ID.

* **GET** `/api/assessments/patient/{patientId}` - Listar avaliações de um paciente.

* **PUT** `/api/assessments/{id}` - Atualizar avaliação.

* **DELETE** `/api/assessments/{id}` - Excluir avaliação.



### 5.4. Gerenciamento de Evoluções

* **POST** `/api/evolutions` - Criar nova evolução clínica.

* **GET** `/api/evolutions` - Listar todas as evoluções.

* **GET** `/api/evolutions/{id}` - Obter evolução por ID.

* **GET** `/api/evolutions/patient/{patientId}` - Listar evoluções de um paciente.

* **PUT** `/api/evolutions/{id}` - Atualizar evolução.

* **DELETE** `/api/evolutions/{id}` - Excluir evolução.



### 5.5. Gerenciamento de Pagamentos (PIX via Mercado Pago)

* **POST** `/api/payments/pix` - Criar pagamento PIX e gerar QR Code.

* **GET** `/api/payments` - Listar todos os pagamentos.

* **GET** `/api/payments/{id}` - Obter pagamento por ID.

* **GET** `/api/payments/patient/{patientId}` - Listar pagamentos de um paciente.

* **POST** `/api/webhooks/mercadopago` - Receber notificações de pagamento (webhook).



## 6. Regras de Negócio (Camada Service)

Estas regras foram implementadas para garantir a integridade do sistema:



1.  **Validação de CPF:** CPF do paciente deve ser único no sistema.

2.  **Validação de Email:** Email do paciente deve ser único no sistema.

3.  **Relacionamento Cascata:** Ao excluir um paciente, todas as suas consultas, avaliações, evoluções e pagamentos são excluídos.

4.  **Integração com Mercado Pago:** Pagamentos são processados via API do Mercado Pago, gerando QR Code PIX para pagamento.

5.  **Webhook de Pagamentos:** Sistema recebe notificações do Mercado Pago para atualizar status de pagamentos automaticamente.



## 7. Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.8** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Validation** - Validação de dados
- **Lombok** - Redução de boilerplate
- **PostgreSQL** - Banco de dados (produção)
- **Mercado Pago SDK** - Integração de pagamentos

### Documentação
- **SpringDoc OpenAPI** - Documentação automática da API (Swagger)

### Build & Dev Tools
- **Maven** - Gerenciamento de dependências e build
- **Spring Boot DevTools** - Hot reload durante desenvolvimento
- **Docker & Docker Compose** - Containerização da aplicação



## 8. Roadmap Técnico



### 1ª Entrega: Estrutura e Persistência

* [x] Configuração do Spring Boot com PostgreSQL.

* [x] Modelagem das entidades e Repositories (Spring Data JPA).

* [x] Implementação dos DTOs e Mappers.

* [x] Camada Service com Regras de Negócio.

* [x] Controllers com documentação (Swagger) e validação (`@Valid`).

* [x] Integração com Mercado Pago (PIX).

* [x] Webhook para notificações de pagamento.

* [x] Dockerização da aplicação (Docker + Docker Compose).



### 2ª Entrega: Segurança e Qualidade

* [ ] Implementação do Spring Security (JWT).

* [ ] Definição de Roles (fisioterapeuta, recepcionista).

* [ ] Testes Unitários e de Integração.

* [ ] Pipeline de CI/CD.

* [ ] Deploy em ambiente de produção.



## 9. Documentação da API

A documentação completa e interativa da API está disponível via Swagger UI:

**URL:** http://localhost:8000/swagger-ui.html



## 10. Comandos Úteis

### Docker

```bash
# Iniciar serviços (build e start)
docker-compose up --build

# Iniciar serviços em background
docker-compose up -d

# Parar serviços
docker-compose down

# Ver logs
docker-compose logs -f

# Rebuild da aplicação
docker-compose build app
```

### Maven (desenvolvimento local)

```bash
# Compilar o projeto
./mvnw compile

# Executar a aplicação
./mvnw spring-boot:run

# Executar testes
./mvnw test

# Gerar JAR
./mvnw clean package

# Limpar build
./mvnw clean
```



## 11. Estrutura do Projeto

```
consultorio-fisio/
├── src/
│   ├── main/
│   │   ├── java/com/joel/consultorio_fisio/
│   │   │   ├── configurations/      # Configurações (OpenAPI, Mercado Pago)
│   │   │   ├── patient/             # Módulo de Pacientes
│   │   │   ├── appointment/         # Módulo de Consultas
│   │   │   ├── assessment/          # Módulo de Avaliações
│   │   │   ├── evolution/           # Módulo de Evoluções
│   │   │   ├── payment/             # Módulo de Pagamentos
│   │   │   └── exception/           # Exceções customizadas
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/                        # Testes (em desenvolvimento)
├── docs/                            # Documentação adicional
├── CLAUDE.md                        # Diretrizes do projeto
├── pom.xml                          # Configuração Maven
└── README.md                        # Este arquivo
```



## 12. Autor

**Joel**

- Estudante de Análise e Desenvolvimento de Sistemas - IFRN
- Desenvolvedor apaixonado por resolver problemas reais



---

**Desenvolvido para a disciplina de Desenvolvimento de Sistemas Corporativos - IFRN**
