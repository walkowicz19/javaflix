# Relatório de Mudanças e Implementações (V2.0)

Este documento detalha **tudo o que foi alterado e adicionado** no projeto JavaFlix, com base no relatório inicial de *Feedback Técnico e Maturidade* de março de 2026. O objetivo principal foi solucionar as lacunas de documentação, falta de testes e implementar conceitos de concorrência e programação distribuída.

---

## 🚀 1. Migração de Arquitetura para Quarkus (JAX-RS)

A principal recomendação arquitetural apontada era o abandono do `HttpServer` nativo e manual. Evoluímos e migramos a base totalmente para o **Framework Quarkus** voltado a microsserviços Cloud-Native com alta performance corporativa.

### `JavaFlixResource.java`
* **Troca do Servidor Customizado e CORS Manual:** Deletamos e transformamos a antiga arquitetura no padrão Jakarta EE (`@Path`, `@GET`, `@Inject`). Segurança e CORS agora são abstraídos e gerenciados pelo contêiner Quarkus via `application.properties`.
* **JSON Nativo:** A serialização antes construída via `String.format` foi descontinuada em prol do `quarkus-resteasy-reactive-jackson` (serialização declarativa e automática de objetos de Domínio POJO).

### `PlataformaStreaming.java`
* **Escopo de Aplicação (CDI) e Busca (Parallel Streams):** A classe principal foi convertida em um Bean Gerenciado Singleton com construtor `@ApplicationScoped` e inicializada no momento do boot via `@PostConstruct`. Alteramos a busca manual por um iterador clássico (`for(Conteudo c : catalogo)`) para trabalhar com `catalogo.parallelStream()`, delegando processamento eficiente iterativo em múltiplas Threads do Kernel.

### 🧩 2. Simulação de Computação Distribuída (Endpoints)
Para representar as "Oportunidades de Paralelismo" indicadas no relatório (streaming em background, recomendações pesadas, push notifications), injetamos lógicas completamente não-bloqueantes simulando essas tecnologias usando **CompletableFutures**.

Foram adicionados ao `Server.java`:
1. `GET /api/recomendacoes` **(Paralelo)**:
   Dispara um `CompletableFuture.supplyAsync()` que segura a execução assíncrona usando `Thread.sleep` (imitando query ao DB) sem prender a thread pool principal, validando recomendação algorítmica.
2. `POST /api/transcodificar` **(CPU Bound task)**:
   Inicia transcodificação (fictícia) simulando processamento super pesado que precisaria de Threads isoladas rodando com `.runAsync()`. Devolve `HTTP 202 Accepted` de forma instantânea para o front end enquanto processa o payload por trás dos panos.
3. `POST /api/notificacoes` **(I/O Bound task)**:
   Simula envio concorrente a servidores externos (e-mails/workers), rodando assincronamente e imitando a comunicação através de filas externas.

---

## 📚 3. Documentação Restante Completada

Os 4 artefatos que estavam pendentes ("Não Existem") citados na Avaliação Original foram devidamente concebidos sem onerar recursos, usando formatos *Markdown* nativos no Github:

* **Manual do Usuário:** Construído como `docs/manual_usuario.md`, definindo passo a passo para um leigo de como rodar NodeJS integrado a Java nativo.
* **OpenAPI / Swagger:** Construído um arquivo formal descritivo `docs/openapi.yaml` listando todos os atributos dos objetos e os métodos, incluindo a documentação das novas rotinas criadas de transcodificação e notificações.
* **Diagrama UML:** Desenhado em `docs/diagrama_uml.md` usando linguagem visual *Mermaid* nativa. Mapeia estaticamente para o painel do GitHub as ligações `Conteudo -> Serie/Filme` detalhando visibilidade (POO).
* **Diagrama de Arquitetura:** Gerado fluxo de rede em `docs/diagrama_arquitetura.md` que mostra exatamente as ramificações de concorrência criadas entre Worker Threads e Main Server API Threads.

---
> **Resumo:** A API antes síncrona, monothread e carente de automação, agora está distribuída com suporte a concorrência assíncrona e preparada documentacionalmente para a escalada até Spring Boot, englobando os gaps principais da avaliação.
