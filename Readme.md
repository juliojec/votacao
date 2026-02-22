# Sistema de Votação Cooperativa

API REST desenvolvida para gerenciamento de pautas e sessões de votação em assembleias cooperativas.

### O sistema permite:

Cadastro de pautas
Abertura de sessões de votação com tempo determinado
Registro de votos por associado
Apuração automática dos resultados
Publicação do resultado final em fila (RabbitMQ)

## Tecnologias Utilizadas

* Java 21
* Spring Boot 3.x
* Swagger UI
* OpenAPI 
* PostgreSQL 16
* RabbitMQ 3.13
* Flyway
* Cache - Caffeine
* Docker & Docker Compose

## Pré-requisitos

- [Docker] 24+
- [Docker Compose] v2+
- [Java 21] *(apenas para rodar localmente sem Docker)*
- [Maven 3.9+] *(apenas para rodar localmente sem Docker)*

---

## Rodando com Docker (recomendado)

### 1. Clone o repositório

```bash
git clone https://github.com/juliojec/votacao.git
cd votacao
```

### 2. Suba todos os serviços via Docker

```bash
docker compose up --build
```

Isso irá:
- Subir o **PostgreSQL** na porta `5432`
- Subir o **RabbitMQ** na porta `5672` (management UI na `15672`)
- Buildar e subir a **aplicação** na porta `8080`
- Executar as **migrações Flyway** automaticamente na inicialização

---

## Rodando localmente (sem Docker)

### 1. Suba apenas a infraestrutura

```bash
docker compose up postgres rabbitmq
```

### 2. Execute a aplicação

```bash
./mvnw spring-boot:run
```

---

## Documentação da API

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

---

## Endpoints principais

| Método | Endpoint                        | Descrição                         |
|--------|---------------------------------|-----------------------------------|
| POST   | `/api/v1/pautas`                | Cadastrar nova pauta              |
| POST   | `/api/v1/sessoes`               | Abrir sessão de votação           |
| POST   | `/api/v1/votos`                 | Registrar voto                    |
| GET    | `/api/v1/pautas/{id}/resultado` | Consultar resultado da votação    |
| GET    | `/api/v1/users/{cpf}`           | Consultar cpf integração externa  |

---

## RabbitMQ

- **Management UI:** `http://localhost:15672`
- **Usuário:** `guest` / **Senha:** `guest`
- **Exchange:** `votacao.exchange`
- **Queue:** `votacao.resultado`

Ao encerrar uma sessão, o seguinte evento é publicado:

```json
{
  "sessaoId": "uuid",
  "pautaId": "uuid",
  "pautaTitulo": "Aprovação do orçamento",
  "votosSim": 10,
  "votosNao": 3,
  "resultado": "APROVADA"
}
```

## Testes

```bash
./mvnw test
```

---
