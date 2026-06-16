# Script para criar documentação consolidada
$content = @"
---
title: "JavaFlix - Documentação Completa do MVP"
subtitle: "Plataforma de Streaming com Programação Concorrente"
author: "Equipe JavaFlix"
date: "21 de Abril de 2026"
version: "1.2.0"
lang: "pt-BR"
---

# JavaFlix - Documentação Completa do MVP

**Plataforma de Streaming com Programação Concorrente**

---

## Informações do Projeto

| Campo | Detalhes |
|-------|----------|
| **Instituição** | Centro Universitário Unieuro |
| **Curso** | Sistemas de Informação |
| **Disciplina** | PROJETO INTEGRADOR DE COMPUTAÇÃO PARALELA |
| **Professor** | [Nome do Professor] |
| **Semestre** | 1º/2026 |
| **Versão** | 1.2.0 |
| **Data de Entrega** | 21/abril/2026 |

---

## Equipe de Desenvolvimento

| Nome Completo | Matrícula | E-mail Acadêmico |
|---------------|-----------|------------------|
| Matheus Nery Walkowicz | [número da matrícula] | matheus.nery@[instituição].edu.br |
| Marcelo Vaz | [número da matrícula] | marcelo.vaz@[instituição].edu.br |
| Gabriel | [número da matrícula] | gabriel@[instituição].edu.br |

---

<div style="page-break-after: always;"></div>

# Sumário Executivo

O **JavaFlix** é uma plataforma de streaming educacional desenvolvida como projeto integrador da disciplina de Programação Concorrente e Distribuída. O sistema demonstra a aplicação prática de conceitos avançados de engenharia de software, incluindo programação orientada a objetos, arquitetura REST, processamento paralelo e desenvolvimento full-stack moderno.

## Visão Geral do Projeto

A solução implementa um backend robusto utilizando **Quarkus Framework** (Java 17+) com suporte a operações concorrentes através de ``parallelStream()`` e ``CompletableFuture``, integrado a um frontend responsivo desenvolvido em **React + TypeScript** com design inspirado em plataformas de streaming profissionais como Netflix.

## Principais Tecnologias

### Backend
- **Java 17+** - Linguagem de programação principal
- **Quarkus 3.x** - Framework supersônico para Java
- **JAX-RS** - API REST
- **CDI** - Injeção de dependência
- **JUnit 5 + Mockito** - Framework de testes

### Frontend
- **React 18** - Biblioteca para interfaces de usuário
- **TypeScript 5.x** - Superset tipado de JavaScript
- **Vite 5.x** - Build tool moderna
- **Tailwind CSS 3.x** - Framework CSS utility-first

### Banco de Dados
- **PocketBase 0.22+** - Backend-as-a-Service
- **SQLite 3.x** - Banco de dados relacional embutido

## Métricas Alcançadas

| Métrica | Meta | Alcançado | Status |
|---------|------|-----------|--------|
| Funcionalidades Obrigatórias | 6/6 | 6/6 | ✅ 100% |
| Cobertura de Testes | ≥70% | ~75% | ✅ Superado |
| Testes Automatizados | ≥15 | 21 | ✅ Superado |
| Endpoints REST | ≥10 | 15+ | ✅ Superado |
| Componentes React | ≥5 | 8 | ✅ Superado |
| Linhas de Código Java | - | ~1.500 | ✅ |
| Linhas de Código TypeScript | - | ~900 | ✅ |
| Documentação | - | 5.000+ linhas | ✅ |

---

<div style="page-break-after: always;"></div>

"@

# Ler conteúdo dos arquivos existentes
$capaContent = Get-Content "javaflix/docs/CAPA_INSTITUCIONAL.md" -Raw -Encoding UTF8
$mvpContent = Get-Content "javaflix/docs/MVP.md" -Raw -Encoding UTF8
$cronogramaContent = Get-Content "javaflix/docs/CRONOGRAMA.md" -Raw -Encoding UTF8
$umlContent = Get-Content "javaflix/docs/DIAGRAMAS_UML.md" -Raw -Encoding UTF8
$bancoContent = Get-Content "javaflix/docs/MODELAGEM_BANCO.md" -Raw -Encoding UTF8

# Concatenar todo o conteúdo
$fullContent = $content + "`n`n<div style=`"page-break-after: always;`"></div>`n`n" + 
               "# 1. Capa Institucional`n`n" + $capaContent + 
               "`n`n<div style=`"page-break-after: always;`"></div>`n`n" +
               "# 2. Definição do MVP`n`n" + $mvpContent +
               "`n`n<div style=`"page-break-after: always;`"></div>`n`n" +
               "# 3. Cronograma de Desenvolvimento`n`n" + $cronogramaContent +
               "`n`n<div style=`"page-break-after: always;`"></div>`n`n" +
               "# 4. Diagramas UML`n`n" + $umlContent +
               "`n`n<div style=`"page-break-after: always;`"></div>`n`n" +
               "# 5. Modelagem de Banco de Dados`n`n" + $bancoContent

# Escrever arquivo final
$fullContent | Out-File -FilePath "javaflix/docs/DOCUMENTACAO_MVP_COMPLETA.md" -Encoding UTF8

Write-Host "Documento consolidado criado com sucesso!"
Write-Host "Total de linhas: $($fullContent.Split("`n").Count)"

# Made with Bob
