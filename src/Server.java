import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class Server {

    private static PlataformaStreaming netflixClone;

    public static void main(String[] args) throws IOException {
        inicializarSistema();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Enable CORS for all contexts
        server.createContext("/api/catalogo", new CatalogoHandler());
        server.createContext("/api/buscar", new BuscaHandler());
        
        server.setExecutor(null); // creates a default executor
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
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    static class CatalogoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204);
                return;
            }

            // We need to access the private list or add a getter. 
            // Since we can't easily modify the other files without potentially breaking Main, 
            // we will use reflection or just add a getter if possible. 
            // Actually, let's just use the 'buscarPorGenero' hack or similar if we can't access catalogo directly.
            // Wait, I can modify PlataformaStreaming to add a getCatalogo() method.
            
            // Assuming I will add getCatalogo() to PlataformaStreaming.java
            List<Conteudo> catalogo = netflixClone.getCatalogo();
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < catalogo.size(); i++) {
                Conteudo c = catalogo.get(i);
                json.append(conteudoToJson(c));
                if (i < catalogo.size() - 1) json.append(",");
            }
            json.append("]");

            sendResponse(exchange, json.toString(), 200);
        }
    }

    static class BuscaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
             if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, "", 204);
                return;
            }
            
            String query = exchange.getRequestURI().getQuery();
            // Simple parsing: ?q=Matrix
            String termo = "";
            if (query != null && query.contains("q=")) {
                termo = query.split("q=")[1];
            }

            try {
                Conteudo c = netflixClone.buscar(termo);
                sendResponse(exchange, conteudoToJson(c), 200);
            } catch (Exception e) { // ConteudoNaoEncontradoException
                sendResponse(exchange, "{\"erro\": \"Conteúdo não encontrado\"}", 404);
            }
        }
    }

    private static String conteudoToJson(Conteudo c) {
        String tipo = (c instanceof Filme) ? "Filme" : "Serie";
        // Escape quotes if necessary, simplified for this demo
        return String.format(
            "{\"titulo\": \"%s\", \"genero\": \"%s\", \"classificacao\": %d, \"tipo\": \"%s\"}",
            c.getTitulo(), c.getGenero(), c.getClassificacaoEtaria(), tipo
        );
    }
}
