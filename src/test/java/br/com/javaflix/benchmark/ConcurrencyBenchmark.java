package br.com.javaflix.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Benchmark JMH para validar ganhos de desempenho com paralelismo.
 *
 * Execução:
 * mvn clean test-compile
 * java -cp target/test-classes;target/classes org.openjdk.jmh.Main br.com.javaflix.benchmark.ConcurrencyBenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ConcurrencyBenchmark {

    private static final int TASK_DURATION_MS = 10;

    @Param({"50", "100", "250", "500"})
    int taskCount;

    private List<String> testData;
    private final List<String> relatorioExecucao = new ArrayList<>();

    @Setup(Level.Trial)
    public void setup() {
        testData = IntStream.range(0, taskCount)
            .mapToObj(i -> "Item-" + i)
            .collect(Collectors.toList());
        relatorioExecucao.clear();
    }

    /**
     * Baseline: processamento sequencial.
     */
    @Benchmark
    public List<String> sequentialProcessing() {
        return testData.stream()
            .map(this::simulateProcessing)
            .collect(Collectors.toList());
    }

    /**
     * Processamento paralelo com parallelStream().
     */
    @Benchmark
    public List<String> parallelStreamProcessing() {
        return testData.parallelStream()
            .map(this::simulateProcessing)
            .collect(Collectors.toList());
    }

    /**
     * Processamento assíncrono com CompletableFuture.
     */
    @Benchmark
    public List<String> completableFutureProcessing() {
        List<CompletableFuture<String>> futures = testData.stream()
            .map(item -> CompletableFuture.supplyAsync(() -> simulateProcessing(item)))
            .collect(Collectors.toList());

        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }

    /**
     * Benchmark para operações de busca sequencial.
     */
    @Benchmark
    public List<String> sequentialSearch() {
        List<String> results = new ArrayList<>();
        for (String item : testData) {
            if (item.contains("5")) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Benchmark para operações de busca paralela.
     */
    @Benchmark
    public List<String> parallelSearch() {
        return testData.parallelStream()
            .filter(item -> item.contains("5"))
            .collect(Collectors.toList());
    }

    /**
     * Benchmark para agregação sequencial.
     */
    @Benchmark
    public long sequentialCount() {
        return testData.stream()
            .filter(item -> item.length() > 5)
            .count();
    }

    /**
     * Benchmark para agregação paralela.
     */
    @Benchmark
    public long parallelCount() {
        return testData.parallelStream()
            .filter(item -> item.length() > 5)
            .count();
    }

    /**
     * Gera relatório manual comparativo para análise de desempenho.
     */
    @TearDown(Level.Trial)
    public void gerarRelatorioComparativo() {
        ResultadoComparativo processamento = medirCenario("Processamento");
        ResultadoComparativo busca = medirCenarioBusca();
        ResultadoComparativo contagem = medirCenarioContagem();

        relatorioExecucao.add(formatarResultado(processamento));
        relatorioExecucao.add(formatarResultado(busca));
        relatorioExecucao.add(formatarResultado(contagem));

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("RELATÓRIO COMPARATIVO DE CONCORRÊNCIA - JAVAFLIX");
        System.out.println("Carga de dados: " + taskCount + " itens");
        System.out.println("==============================================================");
        relatorioExecucao.forEach(System.out::println);
        System.out.println("==============================================================");
    }

    private ResultadoComparativo medirCenario(String nomeCenario) {
        long tempoSequencial = medirTempo(this::executarProcessamentoSequencial);
        long tempoParalelo = medirTempo(this::executarProcessamentoParalelo);
        long tempoCompletableFuture = medirTempo(this::executarProcessamentoCompletableFuture);

        List<ResultadoMetodo> resultados = new ArrayList<>();
        resultados.add(new ResultadoMetodo("Sequencial", tempoSequencial));
        resultados.add(new ResultadoMetodo("Parallel Stream", tempoParalelo));
        resultados.add(new ResultadoMetodo("CompletableFuture", tempoCompletableFuture));

        return new ResultadoComparativo(nomeCenario, resultados);
    }

    private ResultadoComparativo medirCenarioBusca() {
        long tempoSequencial = medirTempo(this::sequentialSearch);
        long tempoParalelo = medirTempo(this::parallelSearch);

        List<ResultadoMetodo> resultados = new ArrayList<>();
        resultados.add(new ResultadoMetodo("Busca Sequencial", tempoSequencial));
        resultados.add(new ResultadoMetodo("Busca Paralela", tempoParalelo));

        return new ResultadoComparativo("Busca", resultados);
    }

    private ResultadoComparativo medirCenarioContagem() {
        long tempoSequencial = medirTempo(this::sequentialCount);
        long tempoParalelo = medirTempo(this::parallelCount);

        List<ResultadoMetodo> resultados = new ArrayList<>();
        resultados.add(new ResultadoMetodo("Contagem Sequencial", tempoSequencial));
        resultados.add(new ResultadoMetodo("Contagem Paralela", tempoParalelo));

        return new ResultadoComparativo("Contagem", resultados);
    }

    private List<String> executarProcessamentoSequencial() {
        return sequentialProcessing();
    }

    private List<String> executarProcessamentoParalelo() {
        return parallelStreamProcessing();
    }

    private List<String> executarProcessamentoCompletableFuture() {
        return completableFutureProcessing();
    }

    private long medirTempo(Runnable bloco) {
        long inicio = System.nanoTime();
        bloco.run();
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
    }

    private String formatarResultado(ResultadoComparativo comparativo) {
        ResultadoMetodo baseline = comparativo.resultados().get(0);
        ResultadoMetodo melhorResultado = comparativo.resultados().stream()
            .min(Comparator.comparingLong(ResultadoMetodo::tempoMs))
            .orElse(baseline);

        StringBuilder builder = new StringBuilder();
        builder.append("Cenário: ").append(comparativo.nomeCenario()).append(System.lineSeparator());

        for (ResultadoMetodo resultado : comparativo.resultados()) {
            builder.append(" - ")
                .append(resultado.nomeMetodo())
                .append(": ")
                .append(resultado.tempoMs())
                .append(" ms");

            if (!resultado.nomeMetodo().equals(baseline.nomeMetodo())) {
                double ganho = calcularGanhoPercentual(baseline.tempoMs(), resultado.tempoMs());
                builder.append(" | ganho vs baseline: ")
                    .append(String.format(Locale.US, "%.2f%%", ganho));
            }

            if (resultado.nomeMetodo().equals(melhorResultado.nomeMetodo())) {
                builder.append(" | melhor resultado");
            }

            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    private double calcularGanhoPercentual(long baselineMs, long tempoAtualMs) {
        if (baselineMs == 0) {
            return 0.0;
        }
        return ((double) (baselineMs - tempoAtualMs) / baselineMs) * 100.0;
    }

    /**
     * Simula processamento de trabalho por item.
     */
    private String simulateProcessing(String item) {
        try {
            Thread.sleep(TASK_DURATION_MS);
            return item.toUpperCase();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return item;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ConcurrencyBenchmark.class.getSimpleName())
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    private record ResultadoComparativo(String nomeCenario, List<ResultadoMetodo> resultados) {
    }

    private record ResultadoMetodo(String nomeMetodo, long tempoMs) {
    }
}

// Made with Bob
