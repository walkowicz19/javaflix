# Manual do Usuário - JavaFlix

## 📖 Índice

- [Visão Geral](#-visão-geral)
- [Primeiros Passos](#-primeiros-passos)
- [Funcionalidades](#-funcionalidades)
- [Navegação](#-navegação)
- [Reprodução de Vídeos](#-reprodução-de-vídeos)
- [Avaliações](#-avaliações)
- [Dicas e Truques](#-dicas-e-truques)
- [Solução de Problemas](#-solução-de-problemas)
- [FAQ](#-faq)

---

## 🎯 Visão Geral

Bem-vindo ao **JavaFlix**, sua plataforma completa de streaming de filmes e séries! 

O JavaFlix oferece:
- ✅ Catálogo extenso de filmes e séries
- ✅ Player de vídeo profissional
- ✅ Sistema de busca inteligente
- ✅ Filtros por gênero
- ✅ Sistema de avaliações
- ✅ Interface moderna e responsiva
- ✅ Autenticação segura (em breve)

---

## 🚀 Primeiros Passos

### 1. Acessando a Plataforma

1. Abra seu navegador (Chrome, Firefox, Edge, Safari)
2. Acesse: **http://localhost:5173**
3. A página inicial será carregada automaticamente

### 2. Navegando pela Interface

A interface do JavaFlix é dividida em seções:

```
┌─────────────────────────────────────┐
│         NAVBAR (Topo)               │
│  [Logo] [Busca] [Navegação]         │
├─────────────────────────────────────┤
│         HERO (Banner)               │
│  Destaque principal com trailer     │
├─────────────────────────────────────┤
│         CATÁLOGO                    │
│  ┌─────────────────────────────┐   │
│  │ Filmes em Destaque          │   │
│  │ [Card] [Card] [Card] ...    │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │ Séries Populares            │   │
│  │ [Card] [Card] [Card] ...    │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## ✨ Funcionalidades

### 🏠 Página Inicial (Home)

#### Banner Principal (Hero)
- **Destaque do Dia:** Conteúdo em evidência
- **Botão "Assistir":** Inicia reprodução imediata
- **Botão "Mais Informações":** Exibe detalhes completos
- **Imagem de Fundo:** Visual atrativo do conteúdo

#### Catálogo Organizado
O catálogo é dividido em categorias:

1. **Filmes em Destaque**
   - Principais filmes disponíveis
   - Ordenados por popularidade

2. **Séries Populares**
   - Séries mais assistidas
   - Novos episódios destacados

3. **Ação e Aventura**
   - Filmes e séries de ação
   - Conteúdo de alta adrenalina

4. **Drama e Suspense**
   - Conteúdo dramático
   - Histórias envolventes

### 🔍 Sistema de Busca

#### Como Buscar

1. **Localização:** Barra de busca no topo da página
2. **Digite:** Nome do filme ou série
3. **Resultados:** Aparecem automaticamente
4. **Clique:** No resultado desejado

#### Dicas de Busca

✅ **Busca Exata:**
```
"Matrix" → Encontra "Matrix"
"Breaking Bad" → Encontra "Breaking Bad"
```

✅ **Busca Parcial:**
```
"Mat" → Encontra "Matrix"
"Break" → Encontra "Breaking Bad"
```

✅ **Case Insensitive:**
```
"matrix" = "Matrix" = "MATRIX"
```

### 🎬 Cards de Conteúdo

Cada card exibe:

```
┌─────────────────┐
│                 │
│   [Thumbnail]   │
│                 │
├─────────────────┤
│ Título          │
│ ⭐ 4.5/5.0     │
│ 🎭 Gênero      │
│ 🔞 Classificação│
├─────────────────┤
│ [▶ Assistir]   │
└─────────────────┘
```

**Informações Exibidas:**
- **Thumbnail:** Imagem do conteúdo
- **Título:** Nome do filme/série
- **Avaliação:** Média de estrelas
- **Gênero:** Categoria (Ação, Drama, etc.)
- **Classificação:** Faixa etária (Livre, 12+, 16+, 18+)
- **Botão Assistir:** Inicia reprodução

### 🎥 Reprodução de Vídeos

#### Acessando o Player

**Método 1: Pelo Card**
1. Localize o conteúdo desejado
2. Clique no botão **"▶ Assistir"**
3. Player abre automaticamente

**Método 2: Pelo Banner**
1. Clique em **"Assistir"** no banner principal
2. Player abre com o conteúdo em destaque

#### Interface do Player

```
┌─────────────────────────────────────┐
│                                     │
│         [VÍDEO EM REPRODUÇÃO]       │
│                                     │
├─────────────────────────────────────┤
│ ◀ Voltar    Título do Conteúdo      │
├─────────────────────────────────────┤
│ ▶/⏸  ⏮ ⏭  🔊  ⚙️  ⛶              │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
│ 00:15 / 02:30                       │
└─────────────────────────────────────┘
```

#### Controles do Player

| Controle | Função | Atalho |
|----------|--------|--------|
| **▶/⏸** | Play/Pause | Espaço |
| **⏮** | Voltar 10s | ← |
| **⏭** | Avançar 10s | → |
| **🔊** | Volume | ↑/↓ |
| **⚙️** | Velocidade | - |
| **⛶** | Tela Cheia | F |
| **◀** | Voltar | ESC |

#### Ajustes de Velocidade

Clique no ícone **⚙️** para ajustar:
- 0.5x (Lento)
- 0.75x
- **1.0x (Normal)**
- 1.25x
- 1.5x
- 2.0x (Rápido)

#### Controle de Volume

1. **Clique no ícone 🔊**
2. **Arraste o slider** para ajustar
3. **Ou use as teclas ↑/↓**

Níveis:
- 🔇 Mudo (0%)
- 🔉 Baixo (1-50%)
- 🔊 Alto (51-100%)

#### Tela Cheia

**Ativar:**
- Clique no ícone **⛶**
- Ou pressione **F**

**Desativar:**
- Pressione **ESC**
- Ou clique novamente no ícone

### ⭐ Sistema de Avaliações

#### Como Avaliar

1. **Assista ao conteúdo**
2. **Clique nas estrelas** (1 a 5)
3. **Avaliação salva automaticamente**

#### Visualizar Avaliações

- **Média Geral:** Exibida no card
- **Número de Avaliações:** Abaixo da média
- **Distribuição:** Em breve

#### Escala de Avaliação

| Estrelas | Significado |
|----------|-------------|
| ⭐ | Muito Ruim |
| ⭐⭐ | Ruim |
| ⭐⭐⭐ | Regular |
| ⭐⭐⭐⭐ | Bom |
| ⭐⭐⭐⭐⭐ | Excelente |

### 🎭 Filtros por Gênero

#### Gêneros Disponíveis

- **Ação** - Filmes de ação e aventura
- **Drama** - Histórias dramáticas
- **Comédia** - Conteúdo humorístico
- **Ficção Científica** - Sci-fi e futurismo
- **Terror** - Filmes de horror
- **Romance** - Histórias românticas
- **Suspense** - Thrillers e mistérios
- **Documentário** - Conteúdo educativo

#### Como Filtrar

1. **Clique no gênero desejado** (em breve)
2. **Resultados filtrados aparecem**
3. **Clique em "Todos"** para remover filtro

---

## 🧭 Navegação

### Menu Principal

| Item | Função |
|------|--------|
| **🏠 Início** | Página principal |
| **🎬 Filmes** | Catálogo de filmes |
| **📺 Séries** | Catálogo de séries |
| **🔍 Buscar** | Busca avançada |
| **👤 Perfil** | Configurações (em breve) |

### Atalhos de Teclado

| Tecla | Ação |
|-------|------|
| **Espaço** | Play/Pause |
| **←** | Voltar 10s |
| **→** | Avançar 10s |
| **↑** | Aumentar volume |
| **↓** | Diminuir volume |
| **F** | Tela cheia |
| **ESC** | Sair da tela cheia |
| **M** | Mudo |

---

## 🎯 Dicas e Truques

### 💡 Dicas de Uso

1. **Busca Rápida**
   - Use palavras-chave curtas
   - Exemplo: "Matrix" ao invés de "The Matrix"

2. **Navegação Eficiente**
   - Use as setas do teclado no player
   - Atalhos economizam tempo

3. **Qualidade de Vídeo**
   - Conexão rápida = melhor qualidade
   - Player ajusta automaticamente

4. **Avaliações Honestas**
   - Ajude outros usuários
   - Avalie após assistir

### 🚀 Recursos Avançados

#### Reprodução Contínua
- Próximo episódio inicia automaticamente (em breve)
- Pode ser desativado nas configurações

#### Lista de Favoritos
- Adicione conteúdos aos favoritos (em breve)
- Acesso rápido aos seus preferidos

#### Histórico de Visualização
- Veja o que já assistiu (em breve)
- Continue de onde parou

---

## 🔧 Solução de Problemas

### Vídeo Não Carrega

**Problema:** Player não inicia reprodução

**Soluções:**
1. ✅ Verifique sua conexão com internet
2. ✅ Recarregue a página (F5)
3. ✅ Limpe o cache do navegador
4. ✅ Tente outro navegador

### Vídeo Travando

**Problema:** Reprodução com pausas

**Soluções:**
1. ✅ Reduza a qualidade do vídeo
2. ✅ Pause e aguarde buffer
3. ✅ Feche outras abas do navegador
4. ✅ Verifique velocidade da internet

### Sem Áudio

**Problema:** Vídeo sem som

**Soluções:**
1. ✅ Verifique volume do player
2. ✅ Verifique volume do sistema
3. ✅ Desative mudo (tecla M)
4. ✅ Teste outro vídeo

### Busca Não Funciona

**Problema:** Busca não retorna resultados

**Soluções:**
1. ✅ Verifique a ortografia
2. ✅ Use termos mais simples
3. ✅ Tente busca parcial
4. ✅ Recarregue a página

### Tela Cheia Não Funciona

**Problema:** Botão de tela cheia não responde

**Soluções:**
1. ✅ Use a tecla F
2. ✅ Clique duas vezes no vídeo
3. ✅ Verifique permissões do navegador
4. ✅ Atualize o navegador

---

## ❓ FAQ (Perguntas Frequentes)

### Geral

**Q: O JavaFlix é gratuito?**  
A: Sim, é uma plataforma acadêmica gratuita.

**Q: Preciso criar conta?**  
A: Atualmente não, mas em breve teremos sistema de contas.

**Q: Funciona em celular?**  
A: Sim, a interface é responsiva e funciona em todos os dispositivos.

**Q: Posso baixar os vídeos?**  
A: Não, apenas streaming online.

### Conteúdo

**Q: Quantos filmes/séries estão disponíveis?**  
A: Atualmente temos 4 conteúdos de demonstração, com mais sendo adicionados.

**Q: Como solicitar um filme/série?**  
A: Em breve teremos sistema de solicitações.

**Q: Conteúdo é atualizado?**  
A: Sim, novos conteúdos são adicionados regularmente.

### Técnico

**Q: Qual navegador é recomendado?**  
A: Chrome, Firefox, Edge ou Safari atualizados.

**Q: Qual velocidade de internet necessária?**  
A: Mínimo 5 Mbps para HD, 25 Mbps para Full HD.

**Q: Suporta quais formatos de vídeo?**  
A: MP4, WebM, YouTube, Vimeo.

**Q: Tem legendas?**  
A: Em desenvolvimento.

### Conta e Perfil

**Q: Como criar uma conta?**  
A: Sistema de contas em desenvolvimento.

**Q: Posso ter múltiplos perfis?**  
A: Funcionalidade planejada para o futuro.

**Q: Como alterar senha?**  
A: Disponível quando sistema de contas estiver ativo.

---

## 📞 Suporte

### Precisa de Ajuda?

**Opção 1: Documentação**
- Consulte este manual
- Veja os guias técnicos em `/docs`

**Opção 2: Issues no GitHub**
- Reporte bugs
- Sugira melhorias
- Tire dúvidas

**Opção 3: Contato Direto**
- Email: suporte@javaflix.com (em breve)
- Discord: JavaFlix Community (em breve)

---

## 🎓 Recursos Educacionais

### Para Desenvolvedores

Este projeto demonstra:
- ✅ Programação Orientada a Objetos
- ✅ Arquitetura REST
- ✅ Processamento Paralelo
- ✅ Frontend Moderno (React)
- ✅ Integração com APIs

### Documentação Técnica

- [README.md](../README.md) - Visão geral do projeto
- [Diagrama de Arquitetura](diagrama_arquitetura.md) - Estrutura do sistema
- [Diagrama UML](diagrama_uml.md) - Classes e relacionamentos
- [OpenAPI](openapi.yaml) - Especificação da API

---

## 🔄 Atualizações Recentes

### Versão 1.0.0 (Atual)

**Novidades:**
- ✅ Player de vídeo completo
- ✅ 4 trailers funcionais
- ✅ Sistema de avaliações
- ✅ Busca inteligente
- ✅ Interface responsiva
- ✅ Integração com PocketBase

**Melhorias:**
- ⚡ Performance otimizada
- 🎨 Design modernizado
- 🔧 Bugs corrigidos

**Em Desenvolvimento:**
- 🔄 Sistema de autenticação
- 🔄 Perfis de usuário
- 🔄 Lista de favoritos
- 🔄 Histórico de visualização

---

## 📊 Estatísticas de Uso

### Diferenciais Técnicos

**Velocidade:**
- ⚡ Processamento paralelo para buscas
- ⚡ Respostas em menos de 100ms
- ⚡ Interface fluida e responsiva

**Qualidade:**
- 🎬 Vídeos em alta definição
- 🎨 Design profissional
- 📱 Compatível com todos os dispositivos

**Segurança:**
- 🔒 Conexões seguras (HTTPS em produção)
- 🔐 Autenticação JWT (em breve)
- 🛡️ Proteção contra ataques

---

## 🎉 Aproveite o JavaFlix!

Agora que você conhece todas as funcionalidades, aproveite ao máximo sua experiência de streaming!

**Dicas Finais:**
1. 🎬 Explore o catálogo completo
2. ⭐ Avalie os conteúdos que assistir
3. 🔍 Use a busca para encontrar seus favoritos
4. 📱 Acesse de qualquer dispositivo
5. 🎯 Aproveite os atalhos de teclado

---

**Última atualização:** 2026-04-05  
**Versão do Manual:** 2.0  
**Status:** ✅ Completo e Atualizado

**Desenvolvido com ❤️ pela equipe JavaFlix**
