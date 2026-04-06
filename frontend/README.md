# JavaFlix Frontend - React + TypeScript + Vite

Este é o frontend do JavaFlix, construído com React, TypeScript e Vite.

## 🚀 Tecnologias

- **React 18** - Biblioteca para construção de interfaces
- **TypeScript** - Superset tipado do JavaScript
- **Vite** - Build tool moderna e rápida
- **Tailwind CSS** - Framework CSS utilitário
- **Axios** - Cliente HTTP para requisições à API

## 📦 Instalação

```bash
npm install
```

## 🔧 Desenvolvimento

Inicie o servidor de desenvolvimento com hot-reload:

```bash
npm run dev
```

O frontend estará disponível em **http://localhost:5173**

## 🏗️ Build para Produção

```bash
npm run build
```

Os arquivos otimizados serão gerados no diretório `dist/`.

## 🧪 Lint

Execute o linter para verificar problemas no código:

```bash
npm run lint
```

## 📁 Estrutura do Projeto

```
frontend/
├── src/
│   ├── components/     # Componentes React
│   │   ├── Hero.tsx    # Banner principal
│   │   ├── Navbar.tsx  # Barra de navegação
│   │   └── Row.tsx     # Linha de conteúdo
│   ├── services/       # Serviços e APIs
│   │   └── api.ts      # Cliente da API
│   ├── assets/         # Recursos estáticos
│   ├── App.tsx         # Componente principal
│   ├── main.tsx        # Ponto de entrada
│   └── types.ts        # Definições de tipos TypeScript
├── public/             # Arquivos públicos
└── Movies and Series/  # Imagens de filmes e séries
```

## 🔌 Integração com Backend

O frontend se comunica com o backend Quarkus através da API REST em `http://localhost:8080/api`.

### Endpoints Utilizados

- `GET /api/catalogo` - Lista todo o catálogo
- `GET /api/buscar?titulo={titulo}` - Busca por título
- `GET /api/filtrar?genero={genero}` - Filtra por gênero
- `GET /api/recomendacoes` - Obtém recomendações (assíncrono)

## 🎨 Personalização

### Tailwind CSS

O projeto usa Tailwind CSS para estilização. Configure em `tailwind.config.js`.

### Temas

As cores e estilos podem ser personalizados no arquivo `src/index.css`.

## 🔧 Configuração do Vite

Este template fornece uma configuração mínima para fazer o React funcionar no Vite com HMR (Hot Module Replacement) e algumas regras do ESLint.

Atualmente, dois plugins oficiais estão disponíveis:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) usa [Babel](https://babeljs.io/) para Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) usa [SWC](https://swc.rs/) para Fast Refresh

## 📝 Expandindo a Configuração do ESLint

Se você está desenvolvendo uma aplicação de produção, recomendamos atualizar a configuração para habilitar regras de lint com verificação de tipos:

```js
export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Outras configurações...

      // Remova tseslint.configs.recommended e substitua por:
      tseslint.configs.recommendedTypeChecked,
      // Alternativamente, use isto para regras mais rigorosas:
      tseslint.configs.strictTypeChecked,
      // Opcionalmente, adicione isto para regras estilísticas:
      tseslint.configs.stylisticTypeChecked,

      // Outras configurações...
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // outras opções...
    },
  },
])
```

Você também pode instalar [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) e [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) para regras de lint específicas do React:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Outras configurações...
      // Habilita regras de lint para React
      reactX.configs['recommended-typescript'],
      // Habilita regras de lint para React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // outras opções...
    },
  },
])
```

## 🚀 Próximas Funcionalidades

- [ ] Player de vídeo integrado
- [ ] Sistema de autenticação
- [ ] Perfis de usuário
- [ ] Lista de favoritos
- [ ] Histórico de visualização
- [ ] Busca avançada com filtros
- [ ] Modo escuro/claro

## 📄 Licença

Projeto acadêmico - Universidade [Nome da Universidade]

---

**Desenvolvido com ❤️ pela equipe JavaFlix**
