package br.com.javaflix.benchmark;

import br.com.javaflix.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de carga com 1.000 usuarios simultaneos no PlataformaStreaming.
 *
 * Cenarios:
 *   1. 1.000 threads lendo catalogo ao mesmo tempo sem ConcurrentModificationException.
 *   2. 1.000 threads buscando titulos em paralelo sem data corruption.
 *   3. 500 threads avaliando conteudos concorrentemente sem race condition.
 *   4. Medicao de throughput: requisicoes por segundo sob carga maxima.
 *
 * BUG ENCONTRADO e CORRIGIDO:
 *   ArrayList em PlataformaStreaming.catalogo e Conteudo.avaliacoes causava
 *   ConcurrentModificationException e ArrayIndexOutOfBoundsException sob carga
 *   concorrente. Substituido por CopyOnWriteArrayList para leitura-intensiva segura.
 */
@DisplayName("Teste de Carga - 1.000 Usuarios Simultaneos")
class ConcurrentLoadTest {

    private static final int USUARIOS_SIMULTANEOS = 1_000;
    private static final int TIMEOUT_SEGUNDOS = 30;

    private PlataformaStreamingTestDouble plataforma;

    @BeforeEach
    void setUp() {
        plataforma = new PlataformaStreamingTestDouble();
    }

    @Test
    @DisplayName("1.000 usuarios lendo o catalogo sem ConcurrentModificationException")
    void testLeituraCatalogoConcorrente() throws InterruptedException {
        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch fim = new CountDownLatch(USUARIOS_SIMULTANEOS);
        AtomicInteger erros = new AtomicInteger(0);
        AtomicInteger sucessos = new AtomicInteger(0);
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < USUARIOS_SIMULTANEOS; i++) {
            new Thread(() -> {
                try {
                    largada.await();
                    List<Conteudo> catalogo = plataforma.getCatalogo();
                    assertNotNull(catalogo);
                    assertFalse(catalogo.isEmpty());
                    catalogo.forEach(c -> assertNotNull(c.getTitulo()));
                    sucessos.incrementAndGet();
                } catch (Exception e) {
                    erros.incrementAndGet();
                    System.err.println("[FALHA getCatalogo] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                } finally {
                    fim.countDown();
                }
            }).start();
        }

        largada.countDown();
        boolean concluiu = fim.await(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        long duracao = System.currentTimeMillis() - inicio;

        System.out.printf("%n[Cenario 1] Leitura Concorrente - %d usuarios em %d ms%n", USUARIOS_SIMULTANEOS, duracao);
        System.out.printf("  Sucessos   : %d%n", sucessos.get());
        System.out.printf("  Erros      : %d%n", erros.get());
        System.out.printf("  Throughput : %.0f req/s%n", (double) USUARIOS_SIMULTANEOS / duracao * 1000);

        assertTrue(concluiu, "Timeout: nem todas as threads terminaram em " + TIMEOUT_SEGUNDOS + "s");
        assertEquals(0, erros.get(),
                "ConcurrentModificationException em " + erros.get() + " threads");
        assertEquals(USUARIOS_SIMULTANEOS, sucessos.get());
    }

    @Test
    @DisplayName("1.000 usuarios buscando titulos em paralelo sem data corruption")
    void testBuscaConcorrente() throws InterruptedException {
        String[] titulos = {
            "O Poderoso Chefao", "Matrix", "Shrek", "Inception",
            "Breaking Bad", "Stranger Things", "La Casa de Papel", "Dark"
        };

        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch fim = new CountDownLatch(USUARIOS_SIMULTANEOS);
        AtomicInteger erros = new AtomicInteger(0);
        AtomicInteger sucessos = new AtomicInteger(0);
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < USUARIOS_SIMULTANEOS; i++) {
            final String titulo = titulos[i % titulos.length];
            new Thread(() -> {
                try {
                    largada.await();
                    Conteudo c = plataforma.buscar(titulo);
                    assertNotNull(c);
                    assertEquals(titulo, c.getTitulo());
                    sucessos.incrementAndGet();
                } catch (ConteudoNaoEncontradoException e) {
                    erros.incrementAndGet();
                } catch (Exception e) {
                    erros.incrementAndGet();
                    System.err.println("[FALHA buscar] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                } finally {
                    fim.countDown();
                }
            }).start();
        }

        largada.countDown();
        boolean concluiu = fim.await(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        long duracao = System.currentTimeMillis() - inicio;

        System.out.printf("%n[Cenario 2] Busca Concorrente - %d usuarios em %d ms%n", USUARIOS_SIMULTANEOS, duracao);
        System.out.printf("  Sucessos   : %d%n", sucessos.get());
        System.out.printf("  Erros      : %d%n", erros.get());
        System.out.printf("  Throughput : %.0f req/s%n", (double) USUARIOS_SIMULTANEOS / duracao * 1000);

        assertTrue(concluiu, "Timeout atingido");
        assertEquals(0, erros.get(), erros.get() + " falhas na busca concorrente");
        assertEquals(USUARIOS_SIMULTANEOS, sucessos.get());
    }

    @Test
    @DisplayName("500 usuarios avaliando o mesmo conteudo sem race condition")
    void testAvaliacaoConcorrente() throws InterruptedException {
        int avaliadores = 500;
        Conteudo filme = plataforma.getCatalogo().get(0);

        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch fim = new CountDownLatch(avaliadores);
        AtomicInteger erros = new AtomicInteger(0);
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < avaliadores; i++) {
            final double nota = (i % 5) + 1.0;
            new Thread(() -> {
                try {
                    largada.await();
                    filme.avaliar(nota);
                } catch (Exception e) {
                    erros.incrementAndGet();
                    System.err.println("[FALHA avaliar] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                } finally {
                    fim.countDown();
                }
            }).start();
        }

        largada.countDown();
        boolean concluiu = fim.await(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        long duracao = System.currentTimeMillis() - inicio;

        double media = filme.obterMediaAvaliacoes();

        System.out.printf("%n[Cenario 3] Avaliacao Concorrente - %d usuarios em %d ms%n", avaliadores, duracao);
        System.out.printf("  Media calculada : %.2f%n", media);
        System.out.printf("  Erros           : %d%n", erros.get());

        assertTrue(concluiu, "Timeout atingido");
        assertEquals(0, erros.get(), erros.get() + " race conditions em avaliar()");
        assertTrue(media > 0 && media <= 5.0,
                "Media invalida (" + media + ") - dados corrompidos pela concorrencia");
    }

    @Test
    @DisplayName("Throughput minimo de 500 req/s com 1.000 usuarios simultaneos")
    void testThroughputMinimo() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(USUARIOS_SIMULTANEOS);
        AtomicInteger concluidos = new AtomicInteger(0);
        AtomicInteger erros = new AtomicInteger(0);
        AtomicLong tempoTotal = new AtomicLong(0);

        CountDownLatch fim = new CountDownLatch(USUARIOS_SIMULTANEOS);
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < USUARIOS_SIMULTANEOS; i++) {
            pool.submit(() -> {
                long t = System.nanoTime();
                try {
                    List<Conteudo> cat = plataforma.getCatalogo();
                    assertNotNull(cat);
                    assertFalse(cat.isEmpty());
                    concluidos.incrementAndGet();
                    tempoTotal.addAndGet(System.nanoTime() - t);
                } catch (Exception e) {
                    erros.incrementAndGet();
                } finally {
                    fim.countDown();
                }
            });
        }

        fim.await(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        long duracaoMs = System.currentTimeMillis() - inicio;
        pool.shutdownNow();

        double throughput = (double) concluidos.get() / duracaoMs * 1000;
        double mediaLatencia = concluidos.get() > 0
                ? TimeUnit.NANOSECONDS.toMicros(tempoTotal.get()) / (double) concluidos.get()
                : 0;

        System.out.printf("%n[Cenario 4] Throughput - %d requisicoes em %d ms%n", USUARIOS_SIMULTANEOS, duracaoMs);
        System.out.printf("  Concluidas    : %d%n", concluidos.get());
        System.out.printf("  Erros         : %d%n", erros.get());
        System.out.printf("  Throughput    : %.0f req/s%n", throughput);
        System.out.printf("  Latencia media: %.1f us/req%n", mediaLatencia);

        assertEquals(0, erros.get(), erros.get() + " erros sob carga de 1.000 req");
        assertTrue(throughput >= 500,
                String.format("Throughput %.0f req/s abaixo do minimo de 500 req/s", throughput));
    }

    /**
     * Subclasse de PlataformaStreaming isolada de CDI para rodar sem servidor Quarkus.
     */
    static class PlataformaStreamingTestDouble extends PlataformaStreaming {
        PlataformaStreamingTestDouble() {
            adicionarConteudo(new Filme("O Poderoso Chefao", "Drama", 16, 175, "Coppola"));
            adicionarConteudo(new Filme("Shrek", "Animacao", 0, 90, "Andrew Adamson"));
            adicionarConteudo(new Filme("Matrix", "Ficcao", 14, 136, "Wachowski"));
            adicionarConteudo(new Filme("Inception", "Ficcao", 14, 148, "Christopher Nolan"));
            adicionarConteudo(new Serie("Breaking Bad", "Drama", 18, 5, 13, 50));
            adicionarConteudo(new Serie("Stranger Things", "Ficcao", 14, 4, 8, 60));
            adicionarConteudo(new Serie("La Casa de Papel", "Acao", 16, 5, 10, 50));
            adicionarConteudo(new Serie("Dark", "Ficcao", 16, 3, 8, 60));
        }
    }
}

// Made with Bob