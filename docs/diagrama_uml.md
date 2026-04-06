# Diagrama UML - Classes JavaFlix

## 📐 Visão Geral

Este documento apresenta os diagramas UML completos do sistema JavaFlix, incluindo:
- Classes de domínio
- DTOs (Data Transfer Objects)
- Services e Resources
- Relacionamentos e dependências

---

## 🎯 Diagrama Principal - Classes de Domínio

```mermaid
classDiagram
    class Avaliavel {
        <<interface>>
        +avaliar(nota: int) void
        +obterMediaAvaliacoes() double
    }

    class Conteudo {
        <<abstract>>
        -String titulo
        -String genero
        -int classificacaoEtaria
        -List~Integer~ avaliacoes
        -String videoUrl
        -String thumbnailUrl
        -String descricao
        +Conteudo(titulo, genero, classificacaoEtaria)
        +avaliar(nota: int) void
        +obterMediaAvaliacoes() double
        +getTitulo() String
        +getGenero() String
        +getClassificacaoEtaria() int
        +getVideoUrl() String
        +setVideoUrl(url: String) void
        +getThumbnailUrl() String
        +setThumbnailUrl(url: String) void
        +getDescricao() String
        +setDescricao(descricao: String) void
    }

    class Filme {
        -int duracaoMinutos
        -String diretor
        +Filme(titulo, genero, classEtaria, duracao, diretor)
        +getDuracaoMinutos() int
        +getDiretor() String
        +toString() String
    }

    class Serie {
        -int temporadas
        -int episodiosPorTemporada
        -int duracaoMediaEpisodioMinutos
        +Serie(titulo, genero, classEtaria, temporadas, episodios, duracaoMedia)
        +getTemporadas() int
        +getEpisodiosPorTemporada() int
        +getDuracaoMediaEpisodioMinutos() int
        +toString() String
    }

    class Usuario {
        -String nome
        -int idade
        -String tipoAssinatura
        -String email
        -String id
        +Usuario(nome, idade, tipoAssinatura)
        +getNome() String
        +getIdade() int
        +getTipoAssinatura() String
        +getEmail() String
        +setEmail(email: String) void
        +getId() String
        +setId(id: String) void
        +podeAssistir(conteudo: Conteudo) boolean
    }

    class PlataformaStreaming {
        -String nome
        -List~Conteudo~ catalogo
        +PlataformaStreaming(nome: String)
        +adicionarConteudo(conteudo: Conteudo) void
        +getCatalogo() List~Conteudo~
        +buscar(titulo: String) Conteudo
        +filtrar(genero: String) List~Conteudo~
        +filtrarPorGenerosParalelo(generos: List~String~) List~Conteudo~
    }

    Avaliavel <|.. Conteudo : implements
    Conteudo <|-- Filme : extends
    Conteudo <|-- Serie : extends
    PlataformaStreaming o-- Conteudo : contains
    PlataformaStreaming ..> Usuario : uses
```

---

## 🔄 Diagrama de DTOs (Data Transfer Objects)

```mermaid
classDiagram
    class ConteudoRecord {
        <<record>>
        +String id
        +String titulo
        +String genero
        +int classificacaoEtaria
        +String tipo
        +String videoUrl
        +String thumbnailUrl
        +String descricao
        +int duracaoMinutos
        +String diretor
        +int temporadas
        +int episodiosPorTemporada
        +double mediaAvaliacoes
    }

    class ConteudoRequest {
        <<record>>
        +String titulo
        +String genero
        +int classificacaoEtaria
        +String tipo
        +String videoUrl
        +String thumbnailUrl
        +String descricao
        +Integer duracaoMinutos
        +String diretor
        +Integer temporadas
        +Integer episodiosPorTemporada
    }

    class ConteudoListResponse {
        <<record>>
        +int page
        +int perPage
        +int totalItems
        +int totalPages
        +List~ConteudoRecord~ items
    }

    class AuthRequest {
        <<record>>
        +String email
        +String password
    }

    class AuthResponse {
        <<record>>
        +String token
        +UserRecord user
    }

    class UserRecord {
        <<record>>
        +String id
        +String email
        +String name
        +String avatar
        +boolean verified
    }

    class UserRequest {
        <<record>>
        +String email
        +String password
        +String passwordConfirm
        +String name
    }

    class AvaliacaoRecord {
        <<record>>
        +String id
        +String conteudoId
        +String userId
        +int nota
        +String comentario
        +String created
    }

    class AvaliacaoRequest {
        <<record>>
        +String conteudoId
        +String userId
        +int nota
        +String comentario
    }

    class AvaliacaoListResponse {
        <<record>>
        +int page
        +int perPage
        +int totalItems
        +int totalPages
        +List~AvaliacaoRecord~ items
    }

    ConteudoListResponse o-- ConteudoRecord : contains
    AuthResponse o-- UserRecord : contains
    AvaliacaoListResponse o-- AvaliacaoRecord : contains
```

---

## 🌐 Diagrama de Resources (REST Endpoints)

```mermaid
classDiagram
    class JavaFlixResource {
        <<@Path("/api/conteudos")>>
        -ConteudoService service
        +listarTodos() Response
        +buscarPorId(id: String) Response
        +buscarPorTitulo(titulo: String) Response
        +filtrarPorGenero(genero: String) Response
        +criar(request: ConteudoRequest) Response
        +atualizar(id: String, request: ConteudoRequest) Response
        +remover(id: String) Response
        +avaliar(id: String, nota: int) Response
        +listarAvaliacoes(id: String) Response
    }

    class AuthResource {
        <<@Path("/api/auth")>>
        -PocketBaseClient client
        +health() Response
        +login(request: AuthRequest) Response
        +register(request: UserRequest) Response
        +verify(token: String) Response
    }

    class ConteudoService {
        <<@ApplicationScoped>>
        -PocketBaseClient client
        +listarTodos() List~Conteudo~
        +listarTodosAsync() CompletableFuture~List~Conteudo~~
        +buscarPorId(id: String) Conteudo
        +buscarPorTitulo(titulo: String) Conteudo
        +filtrarPorGenero(genero: String) List~Conteudo~
        +filtrarPorGenerosParalelo(generos: List~String~) List~Conteudo~
        +criar(request: ConteudoRequest) Conteudo
        +atualizar(id: String, request: ConteudoRequest) Conteudo
        +remover(id: String) void
        +avaliar(id: String, nota: int) void
        +listarAvaliacoes(id: String) List~AvaliacaoRecord~
        -converterParaConteudo(record: ConteudoRecord) Conteudo
        -converterParaRequest(conteudo: Conteudo) ConteudoRequest
    }

    class PocketBaseClient {
        <<@RegisterRestClient>>
        +listarConteudos() ConteudoListResponse
        +buscarConteudo(id: String) ConteudoRecord
        +criarConteudo(request: ConteudoRequest) ConteudoRecord
        +atualizarConteudo(id: String, request: ConteudoRequest) ConteudoRecord
        +removerConteudo(id: String) void
        +login(request: AuthRequest) AuthResponse
        +registrar(request: UserRequest) UserRecord
        +listarAvaliacoes(conteudoId: String) AvaliacaoListResponse
        +criarAvaliacao(request: AvaliacaoRequest) AvaliacaoRecord
    }

    JavaFlixResource --> ConteudoService : uses
    AuthResource --> PocketBaseClient : uses
    ConteudoService --> PocketBaseClient : uses
    ConteudoService ..> Conteudo : creates
    ConteudoService ..> Filme : creates
    ConteudoService ..> Serie : creates
```

---

## 🔐 Diagrama de Segurança

```mermaid
classDiagram
    class CorsFilter {
        <<@Provider>>
        +filter(requestContext, responseContext) void
    }

    class AuthResource {
        -PocketBaseClient client
        +login(request: AuthRequest) Response
        +register(request: UserRequest) Response
        +verify(token: String) Response
    }

    class JWTToken {
        +String token
        +Date expiration
        +String userId
        +validate() boolean
    }

    AuthResource ..> JWTToken : generates
    CorsFilter ..> AuthResource : protects
```

---

## 📊 Diagrama de Relacionamentos Completo

```mermaid
classDiagram
    %% Domain Layer
    class Avaliavel {
        <<interface>>
    }
    class Conteudo {
        <<abstract>>
    }
    class Filme
    class Serie
    class Usuario
    class PlataformaStreaming

    %% Service Layer
    class ConteudoService {
        <<@ApplicationScoped>>
    }

    %% Resource Layer
    class JavaFlixResource {
        <<@Path>>
    }
    class AuthResource {
        <<@Path>>
    }

    %% Client Layer
    class PocketBaseClient {
        <<@RegisterRestClient>>
    }

    %% DTO Layer
    class ConteudoRecord {
        <<record>>
    }
    class ConteudoRequest {
        <<record>>
    }
    class AuthRequest {
        <<record>>
    }
    class AuthResponse {
        <<record>>
    }
    class UserRecord {
        <<record>>
    }

    %% Relationships
    Avaliavel <|.. Conteudo
    Conteudo <|-- Filme
    Conteudo <|-- Serie
    PlataformaStreaming o-- Conteudo
    
    JavaFlixResource --> ConteudoService
    AuthResource --> PocketBaseClient
    ConteudoService --> PocketBaseClient
    ConteudoService ..> Conteudo
    
    PocketBaseClient ..> ConteudoRecord
    PocketBaseClient ..> ConteudoRequest
    PocketBaseClient ..> AuthRequest
    PocketBaseClient ..> AuthResponse
    PocketBaseClient ..> UserRecord
    
    ConteudoService ..> ConteudoRecord
    ConteudoService ..> ConteudoRequest
```

---

## 🎨 Padrões de Design Identificados

### 1. Template Method Pattern

```mermaid
classDiagram
    class Conteudo {
        <<abstract>>
        +avaliar(nota: int)* void
        +obterMediaAvaliacoes()* double
        #calcularMedia() double
    }
    
    class Filme {
        +avaliar(nota: int) void
        +obterMediaAvaliacoes() double
    }
    
    class Serie {
        +avaliar(nota: int) void
        +obterMediaAvaliacoes() double
    }
    
    Conteudo <|-- Filme
    Conteudo <|-- Serie
```

### 2. DTO Pattern

```mermaid
classDiagram
    class Conteudo {
        <<Domain Model>>
        -titulo: String
        -genero: String
        +getTitulo() String
    }
    
    class ConteudoRecord {
        <<DTO>>
        +titulo: String
        +genero: String
    }
    
    class ConteudoService {
        +converterParaConteudo(record) Conteudo
        +converterParaRecord(conteudo) ConteudoRecord
    }
    
    ConteudoService ..> Conteudo
    ConteudoService ..> ConteudoRecord
```

### 3. Service Layer Pattern

```mermaid
classDiagram
    class JavaFlixResource {
        <<Controller>>
        -service: ConteudoService
    }
    
    class ConteudoService {
        <<Service>>
        -client: PocketBaseClient
        +listarTodos() List
        +buscarPorId(id) Conteudo
    }
    
    class PocketBaseClient {
        <<Repository>>
        +listarConteudos() Response
        +buscarConteudo(id) Response
    }
    
    JavaFlixResource --> ConteudoService
    ConteudoService --> PocketBaseClient
```

### 4. Dependency Injection Pattern

```mermaid
classDiagram
    class JavaFlixResource {
        <<@Path>>
        @Inject
        -service: ConteudoService
    }
    
    class ConteudoService {
        <<@ApplicationScoped>>
        @Inject
        -client: PocketBaseClient
    }
    
    class PocketBaseClient {
        <<@RegisterRestClient>>
    }
    
    JavaFlixResource ..> ConteudoService : @Inject
    ConteudoService ..> PocketBaseClient : @Inject
```

---

## 🔄 Diagrama de Sequência - Criar Conteúdo

```mermaid
sequenceDiagram
    participant C as Cliente
    participant R as JavaFlixResource
    participant S as ConteudoService
    participant P as PocketBaseClient
    participant DB as PocketBase

    C->>R: POST /api/conteudos
    activate R
    R->>S: criar(request)
    activate S
    S->>P: criarConteudo(request)
    activate P
    P->>DB: POST /api/collections/conteudos/records
    activate DB
    DB-->>P: ConteudoRecord
    deactivate DB
    P-->>S: ConteudoRecord
    deactivate P
    S->>S: converterParaConteudo(record)
    S-->>R: Conteudo
    deactivate S
    R-->>C: Response 201 Created
    deactivate R
```

---

## 🔄 Diagrama de Sequência - Autenticação

```mermaid
sequenceDiagram
    participant C as Cliente
    participant A as AuthResource
    participant P as PocketBaseClient
    participant DB as PocketBase

    C->>A: POST /api/auth/login
    activate A
    A->>P: login(request)
    activate P
    P->>DB: POST /api/collections/users/auth-with-password
    activate DB
    DB->>DB: Valida credenciais
    DB->>DB: Gera JWT
    DB-->>P: AuthResponse (token + user)
    deactivate DB
    P-->>A: AuthResponse
    deactivate P
    A-->>C: Response 200 OK (token + user)
    deactivate A
```

---

## 📦 Diagrama de Pacotes

```mermaid
graph TB
    subgraph "br.com.javaflix"
        subgraph "domain"
            Avaliavel
            Conteudo
            Filme
            Serie
            Usuario
            PlataformaStreaming
        end
        
        subgraph "resource"
            JavaFlixResource
            AuthResource
        end
        
        subgraph "service"
            ConteudoService
        end
        
        subgraph "client"
            PocketBaseClient
            subgraph "dto"
                ConteudoRecord
                ConteudoRequest
                AuthRequest
                AuthResponse
                UserRecord
            end
        end
        
        subgraph "filter"
            CorsFilter
        end
    end
    
    resource --> service
    service --> client
    service --> domain
    client --> dto
```

---

## 📊 Estatísticas das Classes

| Categoria | Quantidade | Descrição |
|-----------|------------|-----------|
| **Interfaces** | 1 | Avaliavel |
| **Classes Abstratas** | 1 | Conteudo |
| **Classes Concretas** | 5 | Filme, Serie, Usuario, PlataformaStreaming, ConteudoService |
| **Records (DTOs)** | 9 | ConteudoRecord, ConteudoRequest, etc. |
| **Resources** | 2 | JavaFlixResource, AuthResource |
| **Clients** | 1 | PocketBaseClient |
| **Filters** | 1 | CorsFilter |
| **Total** | 20 | Classes/Interfaces/Records |

---

## 🎯 Princípios SOLID Aplicados

### Single Responsibility Principle (SRP)
✅ Cada classe tem uma única responsabilidade:
- `ConteudoService` - Lógica de negócio
- `PocketBaseClient` - Comunicação com API
- `JavaFlixResource` - Endpoints REST

### Open/Closed Principle (OCP)
✅ Classes abertas para extensão, fechadas para modificação:
- `Conteudo` é abstrata, permite novos tipos
- Novos tipos de conteúdo podem ser adicionados sem modificar código existente

### Liskov Substitution Principle (LSP)
✅ Subclasses podem substituir classes base:
- `Filme` e `Serie` podem ser usados onde `Conteudo` é esperado
- Polimorfismo funciona corretamente

### Interface Segregation Principle (ISP)
✅ Interfaces específicas e coesas:
- `Avaliavel` define apenas métodos de avaliação
- Clientes não dependem de métodos que não usam

### Dependency Inversion Principle (DIP)
✅ Dependências de abstrações, não de implementações:
- `ConteudoService` depende de interface `PocketBaseClient`
- Injeção de dependência via CDI

---

## 📝 Conclusão

O diagrama UML do JavaFlix demonstra:

✅ **Hierarquia Clara** - Herança e polimorfismo bem definidos  
✅ **Separação de Responsabilidades** - Camadas distintas  
✅ **Padrões de Design** - DTO, Service Layer, DI  
✅ **SOLID Principles** - Todos os 5 princípios aplicados  
✅ **Extensibilidade** - Fácil adicionar novos tipos de conteúdo  
✅ **Manutenibilidade** - Código organizado e testável  

---

**Última atualização:** 2026-04-05  
**Versão:** 2.0  
**Status:** ✅ Documentação Completa
