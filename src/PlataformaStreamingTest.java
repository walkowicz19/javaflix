public class PlataformaStreamingTest {
    public static void main(String[] args) {
        System.out.println("Iniciando Testes Unitários e de Integração...");
        
        try {
            testarAdicaoEBusca();
            testarBuscaInexistente();
            testarRecomendacaoAsync();
            System.out.println("✅ Todos os testes passaram com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Falha nos testes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testarAdicaoEBusca() throws Exception {
        PlataformaStreaming plataforma = new PlataformaStreaming("TesteFlix");
        Filme filme = new Filme("O Auto da Compadecida", "Comédia", 12, 104, "Guel Arraes");
        plataforma.adicionarConteudo(filme);

        Conteudo encontrado = plataforma.buscar("O Auto da Compadecida");
        if (!encontrado.getTitulo().equals("O Auto da Compadecida")) {
            throw new Exception("Teste AdicaoEBusca Falhou: Título não corresponde");
        }
        System.out.println("✔️ Teste testarAdicaoEBusca passou.");
    }

    private static void testarBuscaInexistente() {
        PlataformaStreaming plataforma = new PlataformaStreaming("TesteFlix");
        boolean falhouComoEsperado = false;
        try {
            plataforma.buscar("Filme Falso");
        } catch (ConteudoNaoEncontradoException e) {
            falhouComoEsperado = true;
        } catch (Exception e) {}
        
        if (!falhouComoEsperado) {
            throw new RuntimeException("Teste BuscaInexistente Falhou: Exception não foi lançada");
        }
        System.out.println("✔️ Teste testarBuscaInexistente passou.");
    }
    
    private static void testarRecomendacaoAsync() throws Exception {
        PlataformaStreaming plataforma = new PlataformaStreaming("TesteFlix");
        plataforma.adicionarConteudo(new Filme("Filme 1", "Ação", 10, 100, "Diretor"));
        plataforma.adicionarConteudo(new Filme("Filme 2", "Ação", 10, 100, "Diretor"));
        plataforma.adicionarConteudo(new Filme("Filme 3", "Ação", 10, 100, "Diretor"));
        
        // Block and wait for async tests
        var recomendacao = plataforma.obterRecomendacoesAsync().get();
        if (recomendacao.size() != 2) {
             throw new Exception("Teste RecomendacaoAsync Falhou: Não retornou 2 recomendacoes");
        }
        System.out.println("✔️ Teste testarRecomendacaoAsync passou.");
    }
}
