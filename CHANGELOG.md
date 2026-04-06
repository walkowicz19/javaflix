# Changelog - JavaFlix

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

## [1.1.0] - 2026-04-06

### ✨ Novas Funcionalidades

#### Sistema de Perfis
- ✅ **Tela de seleção de perfis** estilo Netflix
- ✅ **Gerenciamento de perfis** (criar, editar, excluir)
- ✅ **Até 5 perfis por conta** com avatares personalizados
- ✅ **Perfis infantis** com restrições de conteúdo
- ✅ **Persistência no localStorage** do perfil selecionado
- ✅ **Botão "Trocar Perfil"** no modal de usuário
- ✅ **Avatar do perfil** exibido na Navbar

#### Interface do Usuário
- ✅ **Modal de busca funcional** com filtro em tempo real
- ✅ **Modal de notificações** com 4 notificações de exemplo
- ✅ **Modal de preferências** com 2 abas:
  - Preferências: idioma, qualidade, autoplay, notificações
  - Plano: plano atual, opções de upgrade, método de pagamento
- ✅ **Navegação aprimorada** entre perfis e catálogo

### 🎨 Melhorias de UI/UX

#### VideoPlayer
- ✅ **Controle de volume** com slider oculto por padrão
- ✅ **Cor Netflix red (#E50914)** em todos os controles
- ✅ **Alinhamento perfeito** dos botões de controle
- ✅ **Hover effects** suaves e profissionais

#### Página Watch
- ✅ **Botão de voltar** agora navega para `/browse` (catálogo)
- ✅ **Seção "Informações"** com espaçamento adequado
- ✅ **Layout responsivo** com grid de 2 colunas
- ✅ **Valores padrão** para séries (8 episódios, 60 minutos)

#### Modal de Detalhes (Cards)
- ✅ **Espaçamento corrigido** entre labels e valores
- ✅ **Gap de 32px** para melhor legibilidade
- ✅ **Largura mínima de 240px** para labels
- ✅ **Alinhamento consistente** em todos os campos

### 🔧 Correções de Bugs

- ✅ **Volume slider** não aparecia na hora errada
- ✅ **Cor amarela** removida, substituída por Netflix red
- ✅ **Botão de voltar** agora vai para o catálogo, não para perfis
- ✅ **Espaçamento** dos detalhes corrigido no modal
- ✅ **Avatar do perfil** sincronizado entre Navbar e modal

### 🚀 Melhorias de Performance

- ✅ **Hot Module Replacement (HMR)** funcionando perfeitamente
- ✅ **Lazy loading** de imagens nos cards
- ✅ **Transições suaves** com CSS transitions
- ✅ **Otimização de re-renders** no React

### 📱 Responsividade

- ✅ **Mobile-first design** em todos os componentes
- ✅ **Breakpoints** otimizados para tablets e desktops
- ✅ **Touch gestures** para navegação em mobile
- ✅ **Scroll horizontal** otimizado para cards

### 🎬 Conteúdo

- ✅ **Inception** adicionado ao catálogo
- ✅ **Trailers em PT-BR** para todos os conteúdos
- ✅ **Imagens de alta qualidade** para todos os cards
- ✅ **Metadados completos** (duração, temporadas, episódios)

### 📝 Documentação

- ✅ **README.md** atualizado com novas funcionalidades
- ✅ **CHANGELOG.md** criado para rastrear mudanças
- ✅ **Comentários** adicionados no código
- ✅ **Guias de uso** para novas features

---

## [1.0.0] - 2026-04-05

### 🎉 Lançamento Inicial

#### Backend
- ✅ API REST completa com Quarkus
- ✅ Integração com PocketBase
- ✅ Autenticação JWT
- ✅ CRUD de conteúdos
- ✅ Sistema de avaliações
- ✅ Processamento paralelo
- ✅ 21 testes automatizados

#### Frontend
- ✅ Interface moderna com React + TypeScript
- ✅ Player de vídeo completo
- ✅ Catálogo organizado
- ✅ Design responsivo
- ✅ Tailwind CSS

#### Banco de Dados
- ✅ PocketBase configurado
- ✅ 3 collections (users, conteudos, avaliacoes)
- ✅ 4 conteúdos iniciais
- ✅ Admin UI

---

## Roadmap Futuro

### 🔮 Próximas Versões

#### v1.2.0 (Planejado)
- 🔄 Sistema de favoritos/lista
- 🔄 Histórico de visualização
- 🔄 Recomendações personalizadas
- 🔄 Modo escuro/claro

#### v1.3.0 (Planejado)
- 🔄 Sistema de comentários
- 🔄 Compartilhamento social
- 🔄 Legendas e áudio múltiplo
- 🔄 Download offline

#### v2.0.0 (Futuro)
- 🔄 Live streaming
- 🔄 Chat em tempo real
- 🔄 Gamificação
- 🔄 API pública

---

## Convenções

### Tipos de Mudanças
- ✨ **Novas Funcionalidades** - Novos recursos
- 🎨 **Melhorias de UI/UX** - Design e experiência
- 🔧 **Correções de Bugs** - Bugs corrigidos
- 🚀 **Performance** - Melhorias de velocidade
- 📱 **Responsividade** - Mobile e tablets
- 🎬 **Conteúdo** - Novos filmes/séries
- 📝 **Documentação** - Docs atualizadas
- 🔄 **Em Desenvolvimento** - Work in progress

### Versionamento Semântico
- **MAJOR** (X.0.0) - Mudanças incompatíveis
- **MINOR** (0.X.0) - Novas funcionalidades compatíveis
- **PATCH** (0.0.X) - Correções de bugs

---

**Mantido por:** Equipe JavaFlix  
**Última atualização:** 2026-04-06