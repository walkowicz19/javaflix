# Diagrama de Arquitetura

O sistema JavaFlix utiliza uma arquitetura baseada em **Cliente-Servidor (REST APIs)** com **processamento assíncrono parcial**, demonstrando a evolução de um modelo sequencial básico para um modelo que lida com tarefas concorrentes, assemelhando-se a práticas reais de distribuição em streaming.

```mermaid
graph TD
    Client[Browser / React Frontend]
    Server[API REST - Java HttpServer]
    Busca[Endpoint: Busca e Catálogo]
    AsyncTranscoding[CompletableFuture: Transcodificação Mock]
    AsyncNotifica[CompletableFuture: Notificações Mocks]
    AsyncRecomenda[CompletableFuture: Recomendação]
    DatabaseCache[(Memória - ArrayList)]

    Client -->|HTTP GET /api/catalogo| Busca
    Client -->|HTTP GET /api/recomendacoes| AsyncRecomenda
    Client -->|HTTP POST /api/transcodificar| AsyncTranscoding
    Client -->|HTTP POST /api/notificacoes| AsyncNotifica
    
    Busca -->|Leitura Sequencial| DatabaseCache
    AsyncRecomenda -->|Leitura Paralela| DatabaseCache
    AsyncTranscoding -->|Simula CPU Thread| ThreadsPool((Pool de Threads))
    AsyncNotifica -->|Simula I/O Thread| ThreadsPool
```

## Melhorias Adicionadas
- **ExecutorService / CompletableFutures**: As chamadas que demorariam (transcodificação, envio de e-mails, cálculos de recomendações) agora não bloqueiam o fluxo principal.
- **Segurança**: Headers CORS ajustados e inputs sanitizados.
- **Preparação para o Futuro**: Para resolver a falta de um banco de dados e testes, a estrutura em POO foi desacoplada de forma a facilitar que a classe `PlataformaStreaming` seja substituída futuramente por um repositório JPA / Hibernate ligado ao PostgreSQL ou MySQL.
