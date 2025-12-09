# JavaFlix - Guia de Inicialização

## Introdução

JavaFlix é uma aplicação de streaming que combina um front-end moderno com um back-end em Java. Este guia explica como iniciar o front-end e o back-end para executar a aplicação completa.

## Tecnologias Utilizadas

### Front-end

- **React**: Biblioteca JavaScript para construção de interfaces de usuário.
- **Tailwind CSS**: Framework de CSS para estilização.
- **Vite**: Ferramenta de build rápida para projetos front-end.

### Back-end

- **Java**: Linguagem de programação para o servidor.
- **HttpServer**: Servidor HTTP embutido do Java.

### Dependências

- **Axios**: Biblioteca para requisições HTTP no front-end.
- **Lucide-react**: Ícones para React.

## Passos para Inicialização

### 1. Inicializar o Back-end

1. Certifique-se de ter o **Java JDK** instalado.
2. Navegue até o diretório do back-end:
   ```bash
   cd "c:\Users\user\Downloads\Streaming App\src"
   ```
3. Compile os arquivos Java:
   ```bash
   javac *.java
   ```
4. Inicie o servidor:
   ```bash
   java Server
   ```
5. O servidor estará rodando na porta **8080**.

### 2. Inicializar o Front-end

1. Certifique-se de ter o **Node.js** instalado.
2. Navegue até o diretório do front-end:
   ```bash
   cd "c:\Users\user\Downloads\Streaming App\frontend"
   ```
3. Instale as dependências:
   ```bash
   npm install
   ```
4. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   ```
5. O front-end estará disponível em **http://localhost:5173**.

### 3. Executar a Aplicação Completa

- Certifique-se de que o back-end está rodando na porta **8080**.
- Certifique-se de que o front-end está rodando em **http://localhost:5173**.
- A aplicação estará funcional e exibirá os dados do catálogo de filmes e séries.

## Estrutura do Projeto

### Front-end

- `src/components`: Componentes React (Hero, Navbar, Row).
- `src/services`: Serviços para requisições HTTP.
- `src/types.ts`: Tipos TypeScript para o catálogo.

### Back-end

- `src/Server.java`: Servidor principal.
- `src/PlataformaStreaming.java`: Lógica de negócios para filmes e séries.
- `src/Filme.java` e `src/Serie.java`: Modelos de dados.

## Observações

- Certifique-se de que as portas **8080** (back-end) e **5173** (front-end) estão disponíveis.
- Caso encontre problemas, verifique se as dependências estão instaladas corretamente.
