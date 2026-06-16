# 📚 Documentação Completa - JavaFlix v1.1.0

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura do Sistema](#arquitetura-do-sistema)
3. [Funcionalidades Implementadas](#funcionalidades-implementadas)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Tecnologias Utilizadas](#tecnologias-utilizadas)
6. [Guia de Instalação](#guia-de-instalação)
7. [Guia de Uso](#guia-de-uso)
8. [API Endpoints](#api-endpoints)
9. [Componentes Frontend](#componentes-frontend)
10. [Scripts Utilitários](#scripts-utilitários)
11. [Testes](#testes)
12. [Deploy](#deploy)
13. [Troubleshooting](#troubleshooting)

---

## 🎯 Visão Geral

JavaFlix é uma plataforma de streaming completa desenvolvida como projeto acadêmico, demonstrando conceitos avançados de:

- **Programação Orientada a Objetos** (POO)
- **Arquitetura REST** com Quarkus
- **Frontend Moderno** com React + TypeScript
- **Banco de Dados** com PocketBase
- **Concorrência e Paralelismo** em Java

### Versão Atual: 1.1.0

**Data de Lançamento:** 06/04/2026

**Status:** ✅ Produção

---

## 🏗️ Arquitetura do Sistema

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                     JAVAFLIX ARCHITECTURE                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐      ┌──────────────┐      ┌───────────┐ │
│  │   Frontend   │◄────►│   Backend    │◄────►│ PocketBase│ │
│  │  React + TS  │      │   Quarkus    │      │  SQLite   │ │
│  │  Port: 5173  │      │  Port: 8080  │      │Port: 8090 │ │
│  └──────────────┘      └──────────────┘      └───────────┘ │
│         │                      │                     │       │
│         │                      │                     │       │
│    ┌────▼────┐           ┌────▼────┐          ┌────▼────┐  │
│    │ Vite    │           │ JAX-RS  │          │ REST API│  │
│    │ HMR     │           │ CDI     │          │ Admin UI│  │
│    └─────────┘           └─────────┘          └─────────┘  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Fluxo de Dados

```
User → Frontend → Backend API → PocketBase → SQLite
                      ↓
                 Processamento
                  Paralelo
                      ↓
                  Response
```

---

## ✨ Funcionalidades Implementadas

### 🎭 Sistema de Perfis (v1.1.0)

#### Características:
- ✅ Até 5 perfis por conta
- ✅ Avatares personalizados (8 opções)
- ✅ Perfis infantis com restrições
- ✅ Gerenciamento completo (criar, editar, excluir)
- ✅ Persistência no localStorage
- ✅ Troca de perfil a qualquer momento

#### Componentes:
- `Profiles.tsx` - Tela de seleção e gerenciamento
- `Navbar.tsx` - Exibe avatar do perfil atual
- `App.tsx` - Modal com botão "Trocar Perfil"

#### Fluxo de Uso:
1. Usuário acessa `/` (tela de perfis)
2. Seleciona ou cria um perfil
3. Perfil salvo no localStorage
4. Navega para `/browse` (catálogo)
5. Avatar exibido na Navbar
6. Pode trocar perfil via modal de usuário

---

### 🎬 VideoPlayer Completo

#### Controles Implementados:
- ✅ Play/Pause com overlay central
- ✅ Barra de progresso interativa
- ✅ Controle de volume com slider
- ✅ Botões de skip (±10s)
- ✅ Velocidade de reprodução (0.5x - 2.0x)
- ✅ Fullscreen
- ✅ Auto-hide de controles

#### Características Visuais:
- **Cor Netflix Red** (#E50914) em todos os controles
- **Volume slider** oculto por padrão, aparece no hover
- **Transições suaves** com CSS
- **Responsivo** para mobile e desktop

#### Código:
```typescript
// javaflix/frontend/src/components/VideoPlayer.tsx
<VideoPlayer
  url={videoUrl}
  title={conteudo.titulo}
  onProgress={(progress) => console.log(progress)}
  onEnded={() => console.log('Video ended')}
/>
```

---

### 🔍 Sistema de Busca

#### Funcionalidades:
- ✅ Modal de busca com ícone na Navbar
- ✅ Filtro em tempo real
- ✅ Busca por título
- ✅ Resultados instantâneos
- ✅ Navegação para conteúdo

#### Implementação:
```typescript
// App.tsx
const filteredContent = catalogo.filter(item =>
  item.titulo.toLowerCase().includes(searchQuery.toLowerCase())
);
```

---

### 🔔 Sistema de Notificações

#### Tipos de Notificações:
1. **Novos Conteúdos** - Alertas de lançamentos
2. **Recomendações** - Sugestões personalizadas
3. **Lembretes** - Continuar assistindo
4. **Atualizações** - Novos episódios

#### Exemplo:
```typescript
{
  id: 1,
  type: 'new',
  title: 'Novo na JavaFlix',
  message: 'Stranger Things - Temporada 5 disponível',
  time: '2 horas atrás'
}
```

---

### ⚙️ Preferências de Usuário

#### Abas Disponíveis:

##### 1. Preferências
- **Idioma:** Português (BR), English, Español
- **Qualidade de Vídeo:** Auto, HD, Full HD, 4K
- **Autoplay:** Próximo episódio automático
- **Notificações:** Ativar/desativar alertas

##### 2. Plano
- **Plano Atual:** Premium (R$ 55,90/mês)
- **Opções:**
  - Básico: R$ 25,90/mês (1 tela, SD)
  - Padrão: R$ 39,90/mês (2 telas, HD)
  - Premium: R$ 55,90/mês (4 telas, 4K)
- **Método de Pagamento:** Cartão de crédito
- **Cancelar Assinatura:** Opção disponível

---

### 📺 Catálogo de Conteúdos

#### Conteúdos Disponíveis:

| Título | Tipo | Gênero | Classificação | Trailer |
|--------|------|--------|---------------|---------|
| O Poderoso Chefão | Filme | Drama | 16 anos | ✅ PT-BR |
| Matrix | Filme | Ficção | 14 anos | ✅ PT-BR |
| Inception | Filme | Ficção | 14 anos | ✅ PT-BR |
| Shrek | Filme | Animação | Livre | ✅ PT-BR |
| Breaking Bad | Série | Drama | 18 anos | ✅ PT-BR |
| Stranger Things | Série | Ficção | 14 anos | ✅ PT-BR |
| La Casa de Papel | Série | Ação | 16 anos | ✅ PT-BR |
| Dark | Série | Ficção | 16 anos | ✅ PT-BR |

#### Organização:
- **Início** - Carrossel hero com destaques
- **Filmes em Alta** - Cards grandes com scroll horizontal
- **Séries** - Grid de séries populares
- **Filmes** - Grid de filmes
- **Bombando** - Conteúdos em destaque
- **Minha Lista** - Favoritos do usuário

---

## 📁 Estrutura do Projeto

```
javaflix/
├── frontend/                    # React + TypeScript
│   ├── src/
│   │   ├── components/         # Componentes reutilizáveis
│   │   │   ├── Hero.tsx       # Carrossel principal
│   │   │   ├── Navbar.tsx     # Barra de navegação
│   │   │   ├── Row.tsx        # Linha de cards
│   │   │   └── VideoPlayer.tsx # Player de vídeo
│   │   ├── pages/             # Páginas da aplicação
│   │   │   ├── Profiles.tsx   # Seleção de perfis
│   │   │   └── Watch.tsx      # Página de reprodução
│   │   ├── services/          # Serviços de API
│   │   │   └── api.ts         # Cliente HTTP
│   │   ├── App.tsx            # Componente principal
│   │   ├── main.tsx           # Entry point
│   │   └── types.ts           # Tipos TypeScript
│   ├── public/                # Arquivos estáticos
│   └── package.json           # Dependências npm
│
├── src/main/java/br/com/javaflix/
│   ├── client/                # Cliente PocketBase
│   │   ├── PocketBaseClient.java
│   │   └── dto/              # Data Transfer Objects
│   ├── resource/             # Endpoints REST
│   │   ├── JavaFlixResource.java
│   │   └── AuthResource.java
│   ├── service/              # Lógica de negócio
│   │   └── ConteudoService.java
│   ├── Conteudo.java         # Classe abstrata
│   ├── Filme.java            # Herança de Conteudo
│   ├── Serie.java            # Herança de Conteudo
│   └── Usuario.java          # Modelo de usuário
│
├── src/test/java/            # Testes automatizados
│   ├── benchmark/            # Testes de performance
│   ├── resource/             # Testes de API
│   └── service/              # Testes de serviço
│
├── docs/                     # Documentação
│   ├── diagrama_arquitetura.md
│   ├── diagrama_uml.md
│   ├── guia_deploy.md
│   ├── manual_usuario.md
│   └── openapi.yaml
│
├── pb_data/                  # Dados PocketBase
├── pb_migrations/            # Migrações do banco
│
├── adicionar-trailers-completo.ps1  # Script de trailers
├── adicionar-godfather.ps1          # Script Godfather
├── start-all.ps1                    # Iniciar todos serviços
├── CHANGELOG.md                     # Histórico de mudanças
├── README.md                        # Documentação principal
└── pom.xml                          # Dependências Maven
```

---

## 🛠️ Tecnologias Utilizadas

### Backend

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Java | 17+ | Linguagem principal |
| Quarkus | 3.x | Framework supersônico |
| JAX-RS | 3.x | REST API |
| CDI | 4.x | Injeção de dependência |
| REST Client | 3.x | Cliente HTTP |
| JUnit 5 | 5.x | Testes unitários |
| Mockito | 5.x | Mocks para testes |

### Frontend

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| React | 18.x | Biblioteca UI |
| TypeScript | 5.x | Tipagem estática |
| Vite | 5.x | Build tool |
| Tailwind CSS | 3.x | Framework CSS |
| React Router | 6.x | Roteamento |
| React Player | Latest | Player de vídeo |
| Lucide React | Latest | Ícones |

### Banco de Dados

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| PocketBase | 0.22+ | Backend completo |
| SQLite | 3.x | Banco de dados |

---

## 📦 Guia de Instalação

### Pré-requisitos

```bash
# Verificar versões
java -version    # Java 17+
node -v          # Node 18+
npm -v           # npm 9+
```

### 1. Clonar Repositório

```bash
git clone https://github.com/walkowicz19/javaflix.git
cd javaflix
```

### 2. Instalar PocketBase

#### Windows (PowerShell)
```powershell
# Baixar PocketBase
Invoke-WebRequest -Uri "https://github.com/pocketbase/pocketbase/releases/download/v0.22.0/pocketbase_0.22.0_windows_amd64.zip" -OutFile "pocketbase.zip"
Expand-Archive -Path "pocketbase.zip" -DestinationPath "."

# Iniciar PocketBase
.\pocketbase.exe serve --http="127.0.0.1:8090"
```

#### Linux/Mac
```bash
# Baixar e extrair
wget https://github.com/pocketbase/pocketbase/releases/download/v0.22.0/pocketbase_0.22.0_linux_amd64.zip
unzip pocketbase_0.22.0_linux_amd64.zip

# Iniciar PocketBase
./pocketbase serve --http="127.0.0.1:8090"
```

### 3. Configurar PocketBase

1. Acesse: http://127.0.0.1:8090/_/
2. Crie conta admin
3. Crie collections:
   - `conteudos`
   - `users`
   - `avaliacoes`

### 4. Instalar Dependências Frontend

```bash
cd frontend
npm install
```

### 5. Adicionar Conteúdos

```powershell
# Executar script de trailers
cd ..
.\adicionar-trailers-completo.ps1
```

---

## 🚀 Guia de Uso

### Iniciar Aplicação

#### Opção 1: Script Automático (Recomendado)
```powershell
.\start-all.ps1
```

#### Opção 2: Manual

**Terminal 1 - PocketBase:**
```bash
.\pocketbase.exe serve --http="127.0.0.1:8090"
```

**Terminal 2 - Backend:**
```bash
.\mvnw quarkus:dev
```

**Terminal 3 - Frontend:**
```bash
cd frontend
npm run dev
```

### Acessar Aplicação

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **PocketBase Admin:** http://127.0.0.1:8090/_/
- **Swagger UI:** http://localhost:8080/q/swagger-ui

---

## 🔌 API Endpoints

### Autenticação

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "senha123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "abc123",
    "email": "user@example.com",
    "name": "Usuario"
  }
}
```

### Conteúdos

#### Listar Todos
```http
GET /api/conteudos
```

**Response:**
```json
[
  {
    "id": "1",
    "titulo": "Matrix",
    "tipo": "Filme",
    "genero": "Ficção",
    "classificacao": 14,
    "duracaoMinutos": 136
  }
]
```

#### Buscar por ID
```http
GET /api/conteudos/{id}
```

#### Buscar por Título
```http
GET /api/conteudos/buscar?titulo=Matrix
```

#### Filtrar por Gênero
```http
GET /api/conteudos/filtrar?genero=Ficção
```

#### Criar Conteúdo
```http
POST /api/conteudos
Content-Type: application/json

{
  "titulo": "Novo Filme",
  "tipo": "Filme",
  "genero": "Ação",
  "classificacao": 14,
  "duracaoMinutos": 120
}
```

### Avaliações

#### Avaliar Conteúdo
```http
POST /api/conteudos/{id}/avaliar
Content-Type: application/json

{
  "nota": 5,
  "comentario": "Excelente filme!"
}
```

#### Listar Avaliações
```http
GET /api/conteudos/{id}/avaliacoes
```

---

## 🧩 Componentes Frontend

### Hero Component

**Arquivo:** `frontend/src/components/Hero.tsx`

**Funcionalidade:**
- Carrossel automático de conteúdos em destaque
- Transições suaves entre slides
- Botões "Assistir" e "Mais Informações"
- Background com gradiente

**Props:**
```typescript
interface HeroContent {
  id: string;
  title: string;
  description: string;
  backgroundImage: string;
}
```

**Uso:**
```tsx
<Hero />
```

---

### Navbar Component

**Arquivo:** `frontend/src/components/Navbar.tsx`

**Funcionalidade:**
- Logo JavaFlix
- Menu de navegação
- Ícones de busca, notificações e perfil
- Avatar do perfil atual
- Scroll effect (background transparente → preto)

**Props:**
```typescript
interface NavbarProps {
  onSearch: () => void;
  onNotifications: () => void;
  onUser: () => void;
}
```

**Uso:**
```tsx
<Navbar
  onSearch={() => setShowSearch(true)}
  onNotifications={() => setShowNotifications(true)}
  onUser={() => setShowUserModal(true)}
/>
```

---

### Row Component

**Arquivo:** `frontend/src/components/Row.tsx`

**Funcionalidade:**
- Linha horizontal de cards
- Scroll horizontal suave
- Hover effects
- Drag to scroll (para "Filmes em Alta")
- Lazy loading de imagens

**Props:**
```typescript
interface RowProps {
  title: string;
  items: Conteudo[];
  onCardClick?: (item: Conteudo) => void;
}
```

**Uso:**
```tsx
<Row
  title="Filmes em Alta"
  items={filmes}
  onCardClick={(item) => navigate(`/watch/${item.titulo}`)}
/>
```

---

### VideoPlayer Component

**Arquivo:** `frontend/src/components/VideoPlayer.tsx`

**Funcionalidade:**
- Player completo com controles
- Suporte a YouTube, Vimeo, MP4, WebM
- Controles personalizados Netflix-style
- Fullscreen
- Velocidade de reprodução
- Volume com slider

**Props:**
```typescript
interface VideoPlayerProps {
  url: string;
  title: string;
  onProgress?: (progress: { played: number; playedSeconds: number }) => void;
  onEnded?: () => void;
  initialProgress?: number;
}
```

**Uso:**
```tsx
<VideoPlayer
  url="https://www.youtube.com/watch?v=..."
  title="Matrix"
  onProgress={(progress) => saveProgress(progress)}
  onEnded={() => playNext()}
/>
```

---

### Profiles Component

**Arquivo:** `frontend/src/pages/Profiles.tsx`

**Funcionalidade:**
- Tela de seleção de perfis
- Criar novo perfil (até 5)
- Editar perfis existentes
- Excluir perfis
- 8 avatares disponíveis
- Perfis infantis

**Estado:**
```typescript
interface Profile {
  id: string;
  name: string;
  avatar: string;
  isKid: boolean;
}
```

**Uso:**
```tsx
// Rota: /
<Profiles />
```

---

### Watch Component

**Arquivo:** `frontend/src/pages/Watch.tsx`

**Funcionalidade:**
- Página de reprodução de vídeo
- Informações do conteúdo
- Botões de ação (Gostei, Minha Lista)
- Conteúdos relacionados
- Lista de episódios (para séries)

**Uso:**
```tsx
// Rota: /watch/:id
<Watch />
```

---

## 🔧 Scripts Utilitários

### start-all.ps1

**Descrição:** Inicia todos os serviços (PocketBase, Backend, Frontend)

**Uso:**
```powershell
.\start-all.ps1
```

**Funcionalidade:**
- Inicia PocketBase em background
- Inicia Backend Quarkus
- Inicia Frontend Vite
- Exibe URLs de acesso

---

### adicionar-trailers-completo.ps1

**Descrição:** Adiciona trailers PT-BR a todos os conteúdos no PocketBase

**Uso:**
```powershell
.\adicionar-trailers-completo.ps1
```

**Funcionalidade:**
- Busca todos os conteúdos no PocketBase
- Adiciona trailer_url
- Adiciona image_url
- Marca como destaque
- Exibe relatório de atualização

**Trailers Incluídos:**
- O Poderoso Chefão
- Matrix
- Inception
- Shrek
- Breaking Bad
- Stranger Things
- La Casa de Papel
- Dark

---

### adicionar-godfather.ps1

**Descrição:** Adiciona "O Poderoso Chefão" ao PocketBase

**Uso:**
```powershell
.\adicionar-godfather.ps1
```

**Dados:**
```json
{
  "titulo": "O Poderoso Chefao",
  "tipo": "filme",
  "genero": "Drama, Crime",
  "classificacao_etaria": 16,
  "duracao_minutos": 175,
  "diretor": "Francis Ford Coppola",
  "trailer_url": "https://www.youtube.com/watch?v=sY1S34973zA"
}
```

---

## 🧪 Testes

### Executar Todos os Testes

```bash
./mvnw test
```

### Testes Unitários

```bash
./mvnw test -Dtest=ConteudoServiceTest
```

### Testes de Integração

```bash
./mvnw test -Dtest=AuthResourceTest
```

### Cobertura de Testes

```bash
./mvnw verify
# Relatório em: target/site/jacoco/index.html
```

### Estatísticas

- **Total de Testes:** 21
- **Testes Unitários:** 14
- **Testes de Integração:** 7
- **Cobertura:** ~75%

---

## 🚢 Deploy

### Build para Produção

#### Backend (JAR)
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

#### Backend (Uber-JAR)
```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

#### Backend (Nativo)
```bash
./mvnw package -Dnative
./target/streaming-api-1.0.0-SNAPSHOT-runner
```

#### Frontend
```bash
cd frontend
npm run build
# Arquivos em: dist/
```

### Docker (Futuro)

```bash
docker build -f src/main/docker/Dockerfile.jvm -t javaflix-backend .
docker run -p 8080:8080 javaflix-backend
```

---

## 🐛 Troubleshooting

### Problema: PocketBase não inicia

**Solução:**
```bash
# Verificar se porta 8090 está em uso
netstat -ano | findstr :8090

# Matar processo
taskkill /PID <PID> /F

# Reiniciar PocketBase
.\pocketbase.exe serve --http="127.0.0.1:8090"
```

---

### Problema: Frontend não conecta ao Backend

**Solução:**
1. Verificar se Backend está rodando: http://localhost:8080/api/conteudos
2. Verificar CORS no Backend (`CorsFilter.java`)
3. Verificar URL da API em `frontend/src/services/api.ts`

---

### Problema: Trailers não aparecem

**Solução:**
```powershell
# Executar script de trailers
.\adicionar-trailers-completo.ps1

# Verificar no PocketBase Admin
# http://127.0.0.1:8090/_/
```

---

### Problema: HMR não funciona

**Solução:**
```bash
cd frontend
rm -rf node_modules
npm install
npm run dev
```

---

### Problema: Perfis não salvam

**Solução:**
1. Verificar localStorage no navegador (F12 → Application → Local Storage)
2. Limpar cache: Ctrl+Shift+Delete
3. Recarregar página: Ctrl+F5

---

## 📊 Métricas do Projeto

| Métrica | Valor |
|---------|-------|
| **Linhas de Código Java** | ~2,000 |
| **Linhas de Código TypeScript** | ~1,500 |
| **Linhas de Documentação** | ~6,000 |
| **Testes Automatizados** | 21 |
| **Cobertura de Testes** | ~75% |
| **Collections PocketBase** | 3 |
| **Endpoints REST** | 15+ |
| **Componentes React** | 10 |
| **Páginas** | 3 |
| **Scripts PowerShell** | 3 |

---

## 📝 Changelog

Veja [CHANGELOG.md](CHANGELOG.md) para histórico completo de mudanças.

### v1.1.0 (2026-04-06)
- ✅ Sistema de perfis Netflix-style
- ✅ VideoPlayer com controles completos
- ✅ Modal de busca funcional
- ✅ Modal de notificações
- ✅ Modal de preferências
- ✅ Trailers PT-BR para todos os conteúdos
- ✅ Espaçamento e alinhamento corrigidos

### v1.0.0 (2026-04-05)
- ✅ Lançamento inicial
- ✅ Backend Quarkus completo
- ✅ Frontend React + TypeScript
- ✅ Integração com PocketBase
- ✅ 21 testes automatizados

---

## 🤝 Contribuições

Este é um projeto acadêmico. Para sugestões ou melhorias, consulte a documentação.

---

## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais.

**Universidade:** [Nome da Universidade]  
**Curso:** Engenharia de Software / Ciência da Computação  
**Disciplina:** Programação Orientada a Objetos

---

## 🔗 Links Úteis

- [Quarkus Documentation](https://quarkus.io/guides/)
- [React Documentation](https://react.dev/)
- [PocketBase Documentation](https://pocketbase.io/docs/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS](https://tailwindcss.com/docs)

---

## 👥 Equipe

Desenvolvido com ❤️ pela equipe JavaFlix

---

**Última atualização:** 06/04/2026  
**Versão da Documentação:** 1.0  
**Autor:** Bob (AI Assistant)

---

## 📞 Suporte

Para dúvidas ou problemas, consulte:
1. Esta documentação completa
2. [README.md](README.md)
3. [CHANGELOG.md](CHANGELOG.md)
4. Documentação em `/docs`

---

**🎬 JavaFlix - Streaming de Qualidade com Tecnologia de Ponta! 🚀**