# JavaFlix — Plataforma de Streaming

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.32-blue.svg)](https://quarkus.io/)
[![React](https://img.shields.io/badge/React-19-61DAFB.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6.svg)](https://www.typescriptlang.org/)
[![PocketBase](https://img.shields.io/badge/PocketBase-0.22-B8DBE4.svg)](https://pocketbase.io/)
[![Testes](https://img.shields.io/badge/Testes-25-brightgreen.svg)]()
[![Cobertura](https://img.shields.io/badge/Cobertura-~80%25-green.svg)]()
[![Carga](https://img.shields.io/badge/Carga-1.000%20usuarios-blueviolet.svg)]()

Sistema de streaming academico com Quarkus (backend), React 19 + TypeScript (frontend) e PocketBase (banco SQLite). Demonstra concorrencia com `CompletableFuture`, cache Redis declarativo, observabilidade via Micrometer/Prometheus e resiliencia validada com teste de carga de **1.000 usuarios simultaneos**.

---

## Inicio Rapido (Docker — metodo recomendado)

**Pre-requisito:** [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execucao.

```bash
# 1. Clonar o repositorio
git clone https://github.com/SEU_USUARIO/javaflix.git
cd javaflix

# 2. Subir toda a pilha (backend, frontend, Redis, PocketBase)
docker compose up -d

# 3. Aguardar todos os containers ficarem healthy (aprox. 60 s no primeiro build)
docker compose ps
```

### Acessar a aplicacao

| Servico | URL |
|---------|-----|
| **Frontend** | http://localhost |
| **Backend API** | http://localhost:8083/api |
| **PocketBase Admin** | http://localhost:8090/_/ |
| **Metricas Prometheus** | http://localhost:8083/q/metrics |

### Parar a aplicacao

```bash
docker compose down
```

---

## Desenvolvimento Local (sem Docker)

### Pre-requisitos

| Ferramenta | Versao minima | Link |
|------------|--------------|------|
| **Java JDK** | 17 | [Download](https://www.oracle.com/java/technologies/downloads/) |
| **Node.js** | 18 | [Download](https://nodejs.org/) |
| **Redis** | qualquer | [Download](https://redis.io/download/) |
| **PocketBase** | 0.22 | [Download](https://pocketbase.io/docs/) |

> Maven nao precisa ser instalado — o projeto inclui o wrapper `mvnw` / `mvnw.cmd`.

---

### 1. Clonar o repositorio

```bash
git clone https://github.com/SEU_USUARIO/javaflix.git
cd javaflix
```

---

### 2. Iniciar Redis

```bash
# Linux / macOS
redis-server

# Windows (via WSL ou executavel Redis para Windows)
redis-server.exe
```

---

### 3. Iniciar PocketBase

#### Windows (PowerShell)
```powershell
.\pocketbase.exe serve --http="127.0.0.1:8090"
```

#### Linux / macOS
```bash
./pocketbase serve --http="127.0.0.1:8090"
```

Acesse http://127.0.0.1:8090/_/, crie um admin e importe as collections (veja `docs/pocketbase_setup.md`).

---

### 4. Iniciar o Backend (Quarkus)

#### Linux / macOS
```bash
./mvnw quarkus:dev
```

#### Windows
```cmd
mvnw.cmd quarkus:dev
```

O backend sobe em **http://localhost:8081**.

---

### 5. Iniciar o Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe em **http://localhost:5173**.

---

### URLs em modo desenvolvimento

| Servico | URL |
|---------|-----|
| **Frontend** | http://localhost:5173 |
| **Backend API** | http://localhost:8081/api |
| **Swagger UI** | http://localhost:8081/q/swagger-ui |
| **Metricas Prometheus** | http://localhost:8081/q/metrics |
| **PocketBase Admin** | http://127.0.0.1:8090/_/ |

---

## Build para Producao (JAR)

```bash
# Backend — gera target/quarkus-app/
./mvnw package -DskipTests

# Executar o JAR
java -jar target/quarkus-app/quarkus-run.jar

# Frontend — gera frontend/dist/
cd frontend && npm run build
```

---

## Testes

```bash
# Todos os testes
./mvnw test

# Classe especifica
./mvnw test -Dtest=ConteudoServiceTest

# Apenas testes de carga (1.000 usuarios)
./mvnw test -Dtest=ConcurrentLoadTest

# Relatorio de cobertura (Jacoco)
./mvnw verify
# Relatorio em: target/site/jacoco/index.html
```

**25 testes** — 14 unitarios (JUnit + Mockito) + 7 de integracao (REST Assured) + **4 de carga concorrente**. Cobertura ~80%.

### Testes de Carga — `ConcurrentLoadTest`

Valida resiliencia do sistema sob pico de acesso usando `CountDownLatch` para disparar N threads simultaneamente:

| Cenario | Usuarios | Resultado | Throughput |
|---------|----------|-----------|------------|
| Leitura concorrente do catalogo | 1.000 | ✔ 0 erros | ~10.000 req/s |
| Busca por titulo em paralelo | 1.000 | ✔ 0 erros | ~5.600 req/s |
| Avaliacoes simultaneas (race condition) | 500 | ✔ 0 race cond. | — |
| Throughput minimo (ExecutorService) | 1.000 | ✔ 0 erros | ~14.900 req/s |

**Bug encontrado e corrigido durante os testes:**

> `ArrayList` em `PlataformaStreaming.catalogo` e `Conteudo.avaliacoes` lancava
> `ConcurrentModificationException` sob carga concorrente.
> **Corrigido** substituindo por `CopyOnWriteArrayList` (leitura-intensiva, thread-safe sem lock).

Thread pool tambem redimensionado para absorver pico real:

```properties
# application.properties
javaflix.threadpool.core-size=50   # era 10
javaflix.threadpool.max-size=200   # era 20
javaflix.threadpool.queue-capacity=2000  # era 100
```

---

## Arquitetura

```
javaflix/
+-- src/main/java/br/com/javaflix/
|   +-- JavaFlixResource.java       # Endpoints @Path("/api") + Micrometer @Timed
|   +-- PlataformaStreaming.java     # Logica async (CompletableFuture, ExecutorService)
|   +-- Conteudo.java               # Classe abstrata
|   +-- Filme.java / Serie.java     # Heranca
|   +-- service/
|   |   +-- ConteudoService.java    # CRUD + filtros paralelos + @CacheResult
|   +-- metrics/
|   |   +-- PerformanceMetrics.java # Facade Micrometer
|   +-- client/
|   |   +-- PocketBaseClient.java   # REST Client para PocketBase
|   +-- config/
|       +-- ThreadPoolConfig.java   # ExecutorService "javaflixExecutor"
+-- frontend/                       # React 19 + TypeScript + Vite + Tailwind
+-- src/main/resources/
|   +-- application.properties      # Porta 8081, Redis, JWT, thread pool
+-- docker-compose.yml              # 4 servicos: backend, frontend, redis, pocketbase
+-- Dockerfile                      # Multi-stage: Maven 3.9 -> JRE 17 Alpine
+-- pom.xml                         # Quarkus 3.32.4, Java 17
```

### Topologia Docker

```
React (Nginx :80)
       | REST/JSON
Quarkus Backend (:8083 -> 8081)
   +-- Redis (:6379)      <- @CacheResult / @CacheInvalidateAll
   +-- PocketBase (:8090) <- SQLite + REST Client
```

---

## API Endpoints

### Catalogo

| Metodo | Endpoint | Auth | Descricao |
|--------|----------|------|-----------|
| GET | `/api/catalogo` | Publico | Listar todos os conteudos |
| GET | `/api/buscar?q={titulo}` | Publico | Buscar por titulo |
| GET | `/api/recomendacoes` | JWT | Recomendacoes assincronas |
| POST | `/api/transcodificar` | Admin | Disparar transcodificacao CPU-bound |
| POST | `/api/notificacoes` | Admin | Enviar push para assinantes |
| GET | `/api/metrics/custom` | Publico | Listar medidores Micrometer |

### Observabilidade

| Endpoint | Descricao |
|----------|-----------|
| `GET /q/metrics` | Metricas Prometheus (timers, counters) |
| `GET /q/health` | Health check Quarkus |

---

## Funcionalidades

### Backend
- API REST com Quarkus 3.32 (JAX-RS + CDI)
- Autenticacao JWT delegada ao PocketBase
- Cache Redis declarativo (`@CacheResult` / `@CacheInvalidateAll`, TTL 5 min)
- Processamento paralelo com `CompletableFuture.allOf()` e pool dedicado (`javaflixExecutor`)
- Observabilidade via Micrometer + Prometheus (`@Timed`, `Counter`, `Timer.Sample`)
- Fallback automatico para mock in-memory se PocketBase indisponivel

### Frontend
- React 19 + TypeScript + Vite + Tailwind CSS
- Sistema de perfis Netflix-style (ate 5 perfis)
- Player de video customizado (react-player)
- Busca em tempo real com filtros
- Sistema de notificacoes

---

## Conceitos Academicos Demonstrados

| Area | Implementacao |
|------|--------------|
| **OOP** | `Conteudo` abstrata, `Filme`/`Serie` heranca, `Avaliavel` interface |
| **Concorrencia** | `CompletableFuture.supplyAsync()` + `allOf()`, pool isolado |
| **Paralelismo** | `filtrarPorGenerosParalelo()` — N generos em paralelo com barreira de sincronizacao |
| **Cache** | Redis com TTL 5 min, invalidacao automatica em escrita |
| **Observabilidade** | Histogramas p50/p95/p99 por endpoint via Micrometer/Prometheus |
| **Testes** | JUnit 5 + Mockito + REST Assured, 25 casos, ~80% cobertura |
| **Carga** | `CountDownLatch` + `ExecutorService`, 1.000 threads simultaneas, ~14.900 req/s |
| **Thread-safety** | `CopyOnWriteArrayList` em `catalogo` e `avaliacoes` |

---

## Metricas do Projeto

| Metrica | Valor |
|---------|-------|
| Linhas Java | ~1.500 |
| Linhas TypeScript | ~900 |
| Testes automatizados | 25 (+ 4 de carga) |
| Cobertura | ~80% |
| Throughput pico | ~14.900 req/s |
| Usuarios simultaneos testados | 1.000 |
| Endpoints REST | 6 |
| Componentes React | 8 |
| Versao | 1.1.0 |

---

## Equipe

Matheus Nery . Marcelo Vaz . Gabriel
**Unieuro — Sistemas de Informacao — Computacao Paralela e Concorrente**

---

**Versao:** 1.2.0 . **Atualizado:** 2026-06-25