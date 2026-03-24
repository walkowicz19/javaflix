package br.com.javaflix;

import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;

@ApplicationScoped
public class PlataformaStreaming {
    private String nome;
    private List<Conteudo> catalogo;

    public PlataformaStreaming() {
        this.nome = "JavaFlix";
        this.catalogo = new ArrayList<>();
    }

    @PostConstruct
    void inicializarMock() {
        adicionarConteudo(new Filme("O Poderoso Chefão", "Drama", 16, 175, "Coppola"));
        adicionarConteudo(new Filme("Shrek", "Animação", 0, 90, "Andrew Adamson"));
        adicionarConteudo(new Filme("Matrix", "Ficção", 14, 136, "Wachowski"));
        adicionarConteudo(new Serie("Breaking Bad", "Drama", 18, 5, 13, 50));
        adicionarConteudo(new Serie("Stranger Things", "Ficção", 14, 4, 8, 60));
        adicionarConteudo(new Serie("La Casa de Papel", "Ação", 16, 5, 10, 50));
        adicionarConteudo(new Serie("Dark", "Ficção", 16, 3, 8, 60));
    }


    public void adicionarConteudo(Conteudo conteudo) {
        if (conteudo == null) {
            throw new IllegalArgumentException("Conteúdo não pode ser nulo");
        }
        catalogo.add(conteudo);
    }

    public void listarCatalogo() {
        System.out.println("\n=== Catálogo " + nome + " ===");
        for (Conteudo c : catalogo) {
            System.out.println(c);
        }
    }

    // Busca Paralelizada (Simulação de ganho de performance em catálogos grandes)
    public Conteudo buscar(String titulo) throws ConteudoNaoEncontradoException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título de busca não pode ser vazio");
        }
        
        String tituloLimpo = titulo.trim();
        return catalogo.parallelStream()
                .filter(c -> c.getTitulo().equalsIgnoreCase(tituloLimpo))
                .findFirst()
                .orElseThrow(() -> new ConteudoNaoEncontradoException("Conteúdo '" + tituloLimpo + "' não encontrado."));
    }

    public void assistirConteudo(Usuario usuario, Conteudo conteudo) throws ClassificacaoIndicativaException {
        if (usuario == null || conteudo == null) {
            throw new IllegalArgumentException("Usuário e conteúdo não podem ser nulos");
        }
        if (usuario.getIdade() < conteudo.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException(
                "Usuário " + usuario.getNome() + " não tem idade para assistir '" + conteudo.getTitulo() + "'"
            );
        }
        System.out.println(usuario.getNome() + " está assistindo: " + conteudo.getTitulo());
        System.out.println("Plano: " + usuario.getTipoAssinatura());
    }

    // Filtros paralelizados
    public List<Conteudo> filtrar(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("Gênero não pode ser vazio");
        }
        String generoLimpo = genero.trim();
        return catalogo.parallelStream()
                .filter(c -> c.getGenero().equalsIgnoreCase(generoLimpo))
                .toList();
    }

    public List<Conteudo> filtrar(int idadeMaxima) {
        return catalogo.parallelStream()
                .filter(c -> c.getClassificacaoEtaria() <= idadeMaxima)
                .toList();
    }

    public List<Conteudo> getCatalogo() {
        return catalogo;
    }
    
    // ==========================================
    // SIMULAÇÃO DE SISTEMAS DISTRIBUÍDOS E CONCORRENTES
    // ==========================================

    // 1. Sistema de Recomendação Paralelo
    public java.util.concurrent.CompletableFuture<List<Conteudo>> obterRecomendacoesAsync() {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // Simulando query pesada
            } catch (InterruptedException e) { }
            // Simula recomendação básica (pegar os 2 primeiros aleatórios)
            List<Conteudo> recomendacoes = new ArrayList<>();
            if (catalogo.size() > 2) {
                recomendacoes.add(catalogo.get(0));
                recomendacoes.add(catalogo.get(catalogo.size() - 1));
            }
            return recomendacoes;
        });
    }

    // 2. Mock de Transcodificação de Vídeo (CPU Bound)
    public java.util.concurrent.CompletableFuture<Void> iniciarTranscodificacaoOriginal(Conteudo filme) {
        return java.util.concurrent.CompletableFuture.runAsync(() -> {
            System.out.println("[Transcoder] Iniciando conversão de " + filme.getTitulo() + " para 1080p, 720p...");
            try {
                Thread.sleep(3000); // Simula carga intensa de CPU
            } catch (InterruptedException e) { }
            System.out.println("[Transcoder] Concluído: " + filme.getTitulo());
        });
    }

    // 3. Mock de Envio de Notificações Distribuído (Push)
    public java.util.concurrent.CompletableFuture<Void> notificarUsuariosPush(String mensagem) {
        return java.util.concurrent.CompletableFuture.runAsync(() -> {
            System.out.println("[Notificacao-Worker-1] Lendo fila de disparos...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            System.out.println("[Notificacao-Worker-1] E-mail e Push enviado para todos os assinantes: " + mensagem);
        });
    }

}
