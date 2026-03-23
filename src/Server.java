import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    private static PlataformaStreaming netflixClone;
    private static final int MAX_QUERY_LENGTH = 100;
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-áéíóúÁÉÍÓÚãõÃÕâêôÂÊÔçÇ]+$");

    public static void main(String[] args) throws IOException {
        inicializarSistema();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Enable CORS for all contexts
        server.createContext("/api/catalogo", new CatalogoHandler());
        server.createContext("/api/buscar", new BuscaHandler());
        server.createContext("/api/recomendacoes", new RecomendacoesHandler());
        server.createContext("/api/transcodificar", new TranscodificacaoHandler());
        server.createContext("/api/notificacoes", new NotificacoesHandler());
        
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // Thread pool configurado para paralelizacao
        System.out.println("Servidor rodando na porta " + port);
        server.start();
    }

    private static void inicializarSistema() {
        netflixClone = new PlataformaStreaming("JavaFlix");

        // Populando o catálogo com Filmes e Séries
        netflixClone.adicionarConteudo(new Filme("O Poderoso Chefão", "Drama", 16, 175, "Coppola"));
        netflixClone.adicionarConteudo(new Filme("Shrek", "Animação", 0, 90, "Andrew Adamson"));
        netflixClone.adicionarConteudo(new Filme("Matrix", "Ficção", 14, 136, "Wachowski"));

        netflixClone.adicionarConteudo(new Serie("Breaking Bad", "Drama", 18, 5, 13, 50));
        netflixClone.adicionarConteudo(new Serie("Stranger Things", "Ficção", 14, 4, 8, 60));
        netflixClone.adicionarConteudo(new Serie("La Casa de Papel", "Ação", 16, 5, 10, 50));
        netflixClone.adicionarConteudo(new Serie("Dark", "Ficção", 16, 3, 8, 60));
    }

    // Helper to send JSON response with CORS headers
    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        // Restrict CORS to specific origin instead of wildcard
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        // Security headers
        exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponseHeaders().add("X-XSS-Protection", "1; mode=block");
        
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    // Validate and sanitize user input
    private static String sanitizeInput(String input) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }
        
        String trimmed = input.trim();
        
        if (trimmed.length() > MAX_QUERY_LENGTH) {
            throw new IllegalArgumentException("Input exceeds maximum length");
        }
        
        Matcher matcher = VALID_INPUT_PATTERN.matcher(trimmed);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Input contains invalid characters");
        }
        
        return trimmed;
    }
    
    // Escape JSON strings to prevent XSS
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    static class CatalogoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204);
                return;
            }
            
            // Only allow GET requests
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, "{\"erro\": \"Method not allowed\"}", 405);
                return;
            }

            try {
                List<Conteudo> catalogo = netflixClone.getCatalogo();
                
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < catalogo.size(); i++) {
                    Conteudo c = catalogo.get(i);
                    json.append(conteudoToJson(c));
                    if (i < catalogo.size() - 1) json.append(",");
                }
                json.append("]");

                sendResponse(exchange, json.toString(), 200);
            } catch (Exception e) {
                // Don't expose stack traces to client
                System.err.println("Server error: " + e.getMessage());
                sendResponse(exchange, "{\"erro\": \"Internal server error\"}", 500);
            }
        }
    }

    static class BuscaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
             if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204);
                return;
            }
            
            try {
                String query = exchange.getRequestURI().getQuery();
                
                if (query == null || !query.contains("q=")) {
                    sendResponse(exchange, "{\"erro\": \"Missing search parameter\"}", 400);
                    return;
                }
                
                // Decode and sanitize input
                String termo = URLDecoder.decode(query.split("q=")[1].split("&")[0], StandardCharsets.UTF_8);
                termo = sanitizeInput(termo);

                Conteudo c = netflixClone.buscar(termo);
                sendResponse(exchange, conteudoToJson(c), 200);
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, "{\"erro\": \"Invalid input\"}", 400);
            } catch (ConteudoNaoEncontradoException e) {
                sendResponse(exchange, "{\"erro\": \"Conteúdo não encontrado\"}", 404);
            } catch (Exception e) {
                // Don't expose stack traces to client
                System.err.println("Server error: " + e.getMessage());
                sendResponse(exchange, "{\"erro\": \"Internal server error\"}", 500);
            }
        }
    }

    private static String conteudoToJson(Conteudo c) {
        if (c == null) {
            return "{}";
        }
        String tipo = (c instanceof Filme) ? "Filme" : "Serie";
        // Escape all strings to prevent XSS
        return String.format(
            "{\"titulo\": \"%s\", \"genero\": \"%s\", \"classificacao\": %d, \"tipo\": \"%s\"}",
            escapeJson(c.getTitulo()), escapeJson(c.getGenero()), c.getClassificacaoEtaria(), tipo
        );
    }

    static class RecomendacoesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204); return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, "{\"erro\": \"Method not allowed\"}", 405); return;
            }
            // Usa CompletableFuture fornecendo uma recomendação síncrona/lenta sem travar a pool principal
            netflixClone.obterRecomendacoesAsync().thenAccept(recomendacoes -> {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < recomendacoes.size(); i++) {
                    json.append(conteudoToJson(recomendacoes.get(i)));
                    if (i < recomendacoes.size() - 1) json.append(",");
                }
                json.append("]");
                try {
                    sendResponse(exchange, json.toString(), 200);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    static class TranscodificacaoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204); return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, "{\"erro\": \"Use POST para iniciar transcodificação\"}", 405); return;
            }
            try {
                // Seleciona o primeiro mock e despacha o job
                Conteudo filmeSimulado = netflixClone.getCatalogo().get(0);
                netflixClone.iniciarTranscodificacaoOriginal(filmeSimulado);
                sendResponse(exchange, "{\"mensagem\": \"Transcodificação CPU-bound em background disparada com sucesso.\"}", 202);
            } catch (Exception e) {
                sendResponse(exchange, "{\"erro\": \"Erro\"}", 500);
            }
        }
    }
    
    static class NotificacoesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204); return;
            }
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, "{\"erro\": \"Use POST para disparar notificações\"}", 405); return;
            }
            // Dispara tarefa I/O em background
            netflixClone.notificarUsuariosPush("Lançamento: O Poderoso Chefão 4");
            sendResponse(exchange, "{\"mensagem\": \"Notificações enfileiradas I/O-bound para envio aos usuários.\"}", 202);
        }
    }
}
