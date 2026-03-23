# Manual do Usuário - JavaFlix

## Visão Geral
Bem-vindo ao JavaFlix, a sua plataforma acadêmica de streaming! O JavaFlix permite que você explore um vasto catálogo de filmes e séries, faça buscas, e muito mais.

## Como Acessar
1. Inicie o Servidor Backend (`java Server`).
2. Inicie o Frontend (`npm run dev` na pasta `frontend`).
3. Abra seu navegador e acesse `http://localhost:5173`.

## Funcionalidades Principais

### Tela Inicial (Catálogo)
Na tela inicial, você verá as diferentes seções contendo Filmes e Séries disponíveis no catálogo. Nós dividimos os conteúdos de modo simples e organizado.

### Busca
Você pode usar a barra de busca no topo (quando disponível) ou utilizar os endpoints diretamente para procurar seu título favorito.
- **Dica**: Use o nome exato. Ex: "Matrix" e "Breaking Bad".

### Diferenciais Técnicos
- **Velocidade**: Processamento paralelo usando CompletableFutures para garantir respostas mais rápidas nas buscas e recomendações.
- **Processos em Background**: Sistema assíncrono para tarefas pesadas, como transcodificação de vídeo.

### Próximos Passos
Em futuras versões, teremos contas individuais, perfis diferenciados e um player de vídeo 100% funcional.
