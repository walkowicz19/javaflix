package br.com.javaflix;

import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.concurrent.ExecutorService;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PlataformaStreaming {

    private static final Logger LOG = Logger.getLogger(PlataformaStreaming.class);

    private String nome;
    // CopyOnWriteArrayList: iterações não lançam ConcurrentModificationException
    // mesmo com leitores simultâneos — catálogo é leitura-intensiva (~8 itens fixos).
    private final List<Conteudo> catalogo = new CopyOnWriteArrayList<>();

    @Inject
    @Named("javaflixExecutor")
    ExecutorService executorService;

    public PlataformaStreaming() {
        this.nome = "JavaFlix";
        // catalogo já inicializado como CopyOnWriteArrayList no campo
    }

    @PostConstruct
    void inicializarMock() {
        adicionarConteudo(new Filme("O Poderoso Chefão", "Drama", 16, 175, "Coppola"));
        adicionarConteudo(new Filme("Shrek", "Animação", 0, 90, "Andrew Adamson"));
        adicionarConteudo(new Filme("Matrix", "Ficção", 14, 136, "Wachowski"));
        adicionarConteudo(new Filme("Inception", "Ficção", 14, 148, "Christopher Nolan"));
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

    /**
     * Busca no catálogo in-memory por título (case-insensitive).
     * O catálogo local é pequeno (~8 itens), portanto usa stream sequencial —
     * parallelStream() sobre N < ~1000 elementos tem overhead de fork/join
     * superior ao ganho real.
     */
    public Conteudo buscar(String titulo) throws ConteudoNaoEncontradoException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título de busca não pode ser vazio");
        }
        String tituloLimpo = titulo.trim();
        return catalogo.stream()
                .filter(c -> c.getTitulo().equalsIgnoreCase(tituloLimpo))
                .findFirst()
                .orElseThrow(() -> new ConteudoNaoEncontradoException(
                        "Conteúdo '" + tituloLimpo + "' não encontrado."));
    }

    public void assistirConteudo(Usuario usuario, Conteudo conteudo) throws ClassificacaoIndicativaException {
        if (usuario == null || conteudo == null) {
            throw new IllegalArgumentException("Usuário e conteúdo não podem ser nulos");
        }
        if (usuario.getIdade() < conteudo.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException(
                    "Usuário " + usuario.getNome() + " não tem idade para assistir '"
                            + conteudo.getTitulo() + "'");
        }
        System.out.println(usuario.getNome() + " está assistindo: " + conteudo.getTitulo());
        System.out.println("Plano: " + usuario.getTipoAssinatura());
    }

    /**
     * Filtra o catálogo local por gênero.
     * Stream sequencial: catálogo mock tem ~8 itens; parallelStream adicionaria
     * apenas overhead do ForkJoinPool.
     */
    public List<Conteudo> filtrar(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("Gênero não pode ser vazio");
        }
        String generoLimpo = genero.trim();
        return catalogo.stream()
                .filter(c -> c.getGenero().equalsIgnoreCase(generoLimpo))
                .toList();
    }

    public List<Conteudo> filtrar(int idadeMaxima) {
        return catalogo.stream()
                .filter(c -> c.getClassificacaoEtaria() <= idadeMaxima)
                .toList();
    }

    public List<Conteudo> getCatalogo() {
        return catalogo;
    }

    // ==========================================
    // OPERAÇÕES ASSÍNCRONAS COM PROPÓSITO REAL
    // ==========================================

    /**
     * Gera recomendações de forma assíncrona descarregando o trabalho no pool
     * gerenciado (útil quando a lógica for substituída por chamada I/O real).
     *
     * Algoritmo atual: retorna os dois conteúdos com maior classificação etária
     * (proxy simples de "mais adultos / mais exigentes") sem nenhum sleep artificial.
     */
    public CompletableFuture<List<Conteudo>> obterRecomendacoesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug("[Recomendacoes] Calculando recomendações para catálogo local");
            return catalogo.stream()
                    .sorted(Comparator.comparingInt(Conteudo::getClassificacaoEtaria).reversed())
                    .limit(2)
                    .collect(java.util.stream.Collectors.toList());
        }, executorService);
    }

    /**
     * Dispara a geração de metadados / checksum de um conteúdo em background.
     *
     * Substitui o sleep de 3 s por trabalho real de CPU: calcula o SHA-256 do
     * título serializado em bytes — operação trivial em volume, mas demonstra
     * uso honesto da thread sem bloquear I/O artificialmente.
     *
     * Em produção este método receberia o caminho do arquivo e faria a leitura
     * do stream real; a assinatura pública permanece compatível.
     */
    public CompletableFuture<Void> iniciarTranscodificacao(Conteudo conteudo) {
        return CompletableFuture.runAsync(() -> {
            LOG.infof("[Transcoder] Processando metadados de '%s'", conteudo.getTitulo());
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                // Processa o título + gênero + classificação como payload representativo
                byte[] input = (conteudo.getTitulo()
                        + conteudo.getGenero()
                        + conteudo.getClassificacaoEtaria()).getBytes(java.nio.charset.StandardCharsets.UTF_8);
                // Repete o hash 10.000 vezes para representar carga de metadados
                for (int i = 0; i < 10_000; i++) {
                    input = digest.digest(input);
                }
                String checksum = bytesToHex(input);
                LOG.infof("[Transcoder] Metadados prontos para '%s' — checksum: %s",
                        conteudo.getTitulo(), checksum);
            } catch (java.security.NoSuchAlgorithmException e) {
                LOG.error("[Transcoder] SHA-256 indisponível", e);
            }
        }, executorService);
    }

    /**
     * Mantém compatibilidade com o endpoint /transcodificar existente.
     */
    public CompletableFuture<Void> iniciarTranscodificacaoOriginal(Conteudo conteudo) {
        return iniciarTranscodificacao(conteudo);
    }

    /**
     * Enfileira notificação push para todos os assinantes.
     *
     * Sem sleep artificial: constrói o payload JSON da notificação e registra
     * via logging — substitua o LOG.infof pelo envio real (HTTP/WebSocket/Kafka)
     * sem alterar a assinatura pública.
     */
    public CompletableFuture<Void> notificarUsuariosPush(String mensagem) {
        return CompletableFuture.runAsync(() -> {
            LOG.info("[Notificacao] Montando payload de push para todos os assinantes...");
            String payload = String.format(
                    "{\"tipo\":\"push\",\"mensagem\":\"%s\",\"timestamp\":%d,\"destinatarios\":\"all\"}",
                    mensagem.replace("\"", "\\\""),
                    System.currentTimeMillis());
            // Aqui seria feita a chamada real ao serviço de push (FCM, APNs, WebSocket…)
            LOG.infof("[Notificacao] Push enfileirado — payload: %s", payload);
        }, executorService);
    }

    // ──────────────────────────────────────────
    // Utilitários internos
    // ──────────────────────────────────────────

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
