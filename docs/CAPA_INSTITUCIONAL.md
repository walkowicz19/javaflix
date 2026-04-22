╔══════════════════════════════════════════════════════════════════════════╗
║                                                                          ║
║                          CENTRO UNIVERSITÁRIO                            ║
║                      [NOME DA INSTITUIÇÃO - UNIEURO]                     ║
║                                                                          ║
║                    CURSO DE ENGENHARIA DE SOFTWARE /                     ║
║                        CIÊNCIA DA COMPUTAÇÃO                             ║
║                                                                          ║
╚══════════════════════════════════════════════════════════════════════════╝
```

---

<div align="center">

# JAVAFLIX
## Plataforma de Streaming com Programação Concorrente

### Documentação Técnica e Acadêmica

</div>

---

## 📋 Informações Institucionais

| Campo | Informação |
|-------|------------|
| **Instituição** | Centro Universitário Unieuro |
| **Curso** | Sistemas de Informação |
| **Disciplina** | PROJETO INTEGRADOR DE COMPUTAÇÃO PARALELA |
| **Professor** | [Nome do Professor] |
| **Semestre** | 1º/2026 |

---

## 👥 Equipe de Desenvolvimento

| Nome Completo | Matrícula | E-mail Acadêmico |
|---------------|-----------|------------------|
| Matheus Nery Walkowicz | [número da matrícula] | matheus.nery@[instituição].edu.br |
| Marcelo Vaz | [número da matrícula] | marcelo.vaz@[instituição].edu.br |
| Gabriel | [número da matrícula] | gabriel@[instituição].edu.br |

---

## 📄 Informações do Projeto

| Campo | Detalhes |
|-------|----------|
| **Título** | JavaFlix - Plataforma de Streaming com Programação Concorrente |
| **Subtítulo** | Sistema Completo de Streaming Educacional com Arquitetura REST e Processamento Paralelo |
| **Versão** | 1.2.0 |
| **Data de Entrega** | 21/abril/2026 |
| **Tipo de Projeto** | Projeto Integrador - Trabalho Acadêmico |
| **Área de Conhecimento** | Programação Concorrente, Arquitetura de Software, Desenvolvimento Web Full-Stack |

---

## 📝 Resumo Executivo

O **JavaFlix** é uma plataforma de streaming educacional desenvolvida como projeto integrador da disciplina de Programação Concorrente e Distribuída. O sistema demonstra a aplicação prática de conceitos avançados de engenharia de software, incluindo programação orientada a objetos, arquitetura REST, processamento paralelo e desenvolvimento full-stack moderno.

A solução implementa um backend robusto utilizando **Quarkus Framework** (Java 17+) com suporte a operações concorrentes através de `parallelStream()` e `CompletableFuture`, integrado a um frontend responsivo desenvolvido em **React + TypeScript** com design inspirado em plataformas de streaming profissionais. O sistema utiliza **PocketBase** como backend-as-a-service para persistência de dados e autenticação JWT.

**Principais Tecnologias:** Java 17, Quarkus 3.x, React 18, TypeScript 5.x, PocketBase 0.22+, Tailwind CSS, JUnit 5, Mockito, REST Assured.

**Objetivos Alcançados:** Sistema funcional com autenticação, gerenciamento de perfis, catálogo de conteúdos, busca avançada, player de vídeo integrado, sistema de avaliações e cobertura de testes de ~75%.

---

## 📊 Histórico de Versões

| Versão | Data | Descrição | Responsável |
|--------|------|-----------|-------------|
| **1.0.0** | 05/abril/2026 | Entrega inicial do projeto com funcionalidades core implementadas | Equipe JavaFlix |
| **1.1.0** | 06/abril/2026 | Correções de bugs, melhorias de interface e sistema de perfis completo | Equipe JavaFlix |
| **1.2.0** | 21/abril/2026 | Documentação acadêmica completa, definição formal do MVP e cronograma detalhado | Equipe JavaFlix |

---

## 🎯 Escopo do Projeto

### Funcionalidades Implementadas

#### Backend (API REST)
- ✅ Autenticação e autorização com JWT
- ✅ CRUD completo de conteúdos (filmes e séries)
- ✅ Sistema de avaliações com cálculo de média
- ✅ Busca e filtros por título e gênero
- ✅ Processamento paralelo com `parallelStream()`
- ✅ Operações assíncronas com `CompletableFuture`
- ✅ Integração com PocketBase via REST Client
- ✅ Tratamento robusto de erros e exceções

#### Frontend (Interface Web)
- ✅ Interface moderna estilo Netflix com Tailwind CSS
- ✅ Sistema de perfis (até 5 perfis por conta)
- ✅ Player de vídeo com controles completos
- ✅ Catálogo organizado por categorias
- ✅ Busca em tempo real com modal funcional
- ✅ Sistema de notificações integrado
- ✅ Design responsivo (mobile e desktop)
- ✅ Suporte a múltiplos formatos de vídeo

#### Qualidade e Testes
- ✅ 21 testes automatizados (14 unitários + 7 integração)
- ✅ Cobertura de código de aproximadamente 75%
- ✅ Testes de concorrência validados
- ✅ Mocks e stubs implementados com Mockito

---

## 📈 Métricas do Projeto

| Métrica | Valor |
|---------|-------|
| **Linhas de Código Java** | ~1.500 |
| **Linhas de Código TypeScript** | ~900 |
| **Linhas de Documentação** | ~5.000+ |
| **Testes Automatizados** | 21 |
| **Cobertura de Testes** | ~75% |
| **Collections PocketBase** | 3 (users, conteudos, avaliacoes) |
| **Endpoints REST** | 15+ |
| **Componentes React** | 8 |
| **Tempo de Desenvolvimento** | ~8 semanas |

---

## 🏆 Conceitos Acadêmicos Demonstrados

### Programação Orientada a Objetos
- **Herança:** Classes `Filme` e `Serie` herdam de `Conteudo` abstrata
- **Polimorfismo:** Interface `Avaliavel` implementada por múltiplas classes
- **Encapsulamento:** Atributos privados com getters/setters apropriados
- **Abstração:** Classe abstrata `Conteudo` define contrato comum

### Concorrência e Paralelismo
- **parallelStream():** Busca e filtros executados em paralelo
- **CompletableFuture:** Operações assíncronas não-bloqueantes
- **Thread Safety:** Sincronização adequada em operações críticas
- **Processamento Paralelo:** Utilização eficiente de múltiplas threads

### Arquitetura e Design Patterns
- **REST API:** Endpoints bem definidos seguindo padrões RESTful
- **DTO Pattern:** Separação clara entre camadas de apresentação e domínio
- **Service Layer:** Lógica de negócio isolada dos recursos REST
- **Dependency Injection:** CDI do Quarkus para inversão de controle
- **Error Handling:** Tratamento centralizado de exceções

---

## 📚 Estrutura da Documentação

Este projeto inclui documentação completa e abrangente:

| Documento | Descrição | Localização |
|-----------|-----------|-------------|
| **README.md** | Visão geral e guia de início rápido | `/javaflix/README.md` |
| **CAPA_INSTITUCIONAL.md** | Este documento - informações acadêmicas | `/javaflix/docs/CAPA_INSTITUCIONAL.md` |
| **MVP.md** | Definição formal do Produto Mínimo Viável | `/javaflix/docs/MVP.md` |
| **CRONOGRAMA.md** | Cronograma detalhado do projeto | `/javaflix/docs/CRONOGRAMA.md` |
| **DOCUMENTACAO_COMPLETA.md** | Documentação técnica completa | `/javaflix/DOCUMENTACAO_COMPLETA.md` |
| **diagrama_uml.md** | Diagramas UML de classes e casos de uso | `/javaflix/docs/diagrama_uml.md` |
| **diagrama_arquitetura.md** | Arquitetura do sistema | `/javaflix/docs/diagrama_arquitetura.md` |
| **manual_usuario.md** | Manual do usuário final | `/javaflix/docs/manual_usuario.md` |
| **openapi.yaml** | Especificação OpenAPI da API | `/javaflix/docs/openapi.yaml` |
| **CHANGELOG.md** | Histórico de mudanças | `/javaflix/CHANGELOG.md` |

---

## 🔗 Links e Recursos

### Repositório e Código
- **Repositório Git:** [URL do repositório]
- **Documentação Online:** [URL da documentação]
- **Demo/Apresentação:** [URL da demo]

### Tecnologias Utilizadas
- **Quarkus Framework:** https://quarkus.io/
- **React:** https://react.dev/
- **PocketBase:** https://pocketbase.io/
- **TypeScript:** https://www.typescriptlang.org/
- **Tailwind CSS:** https://tailwindcss.com/

---

## 📞 Contato

Para dúvidas, sugestões ou informações adicionais sobre o projeto:

**E-mail da Equipe:** [email-equipe]@[instituição].edu.br  
**Professor Orientador:** [email-professor]@[instituição].edu.br

---

## 📜 Declaração de Autenticidade

Declaramos que este trabalho é original e foi desenvolvido exclusivamente pela equipe identificada neste documento, sob orientação do professor da disciplina. Todas as fontes consultadas foram devidamente citadas e referenciadas.

---

**Local:** Brasília, DF  
**Data:** 21 de abril de 2026

---

## ✍️ Assinaturas

```
_________________________________
Matheus Nery Walkowicz
Matrícula: [número]


_________________________________
Marcelo Vaz
Matrícula: [número]


_________________________________
Gabriel
Matrícula: [número]


_________________________________
[Nome do Professor]
Professor Orientador
```

---

<div align="center">

**CENTRO UNIVERSITÁRIO [NOME DA INSTITUIÇÃO]**  
**Brasília - DF**  
**2026**

</div>