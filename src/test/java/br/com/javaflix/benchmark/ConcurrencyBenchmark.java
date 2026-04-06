package br.com.javaflix.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Benchmark JMH para validar ganhos de desempenho com paralelismo.
 * 
 * Execução:
 * mvn clean test-compile
 * java -cp target/test-classes:target/classes org.openjdk.jmh.Main ConcurrencyBenchmark
 * 
 * Ou adicionar profile no pom.xml para executar com: mvn test -Pbenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ConcurrencyBenchmark {

    private static final int TASK_COUNT = 100;
    private static final int TASK_DURATION_MS = 10;
    
    private List<String> testData;
    
    @Setup
    public void setup() {
        testData = IntStream.range(0, TASK_COUNT)
            .mapToObj(i -> "Item-" + i)
            .collect(Collectors.toList());
    }
    
    /**
     * Baseline: Processamento sequencial
     */
    @Benchmark
    public List<String> sequentialProcessing() {
        return testData.stream()
            .map(this::simulateProcessing)
            .collect(Collectors.toList());
    }
    
    /**
     * Processamento paralelo com parallelStream()
     */
    @Benchmark
    public List<String> parallelStreamProcessing() {
        return testData.parallelStream()
            .map(this::simulateProcessing)
            .collect(Collectors.toList());
    }
    
    /**
     * Processamento assíncrono com CompletableFuture
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
     * Simula processamento CPU-bound
     */
    private String simulateProcessing(String item) {
        try {
            // Simula trabalho CPU-bound
            Thread.sleep(TASK_DURATION_MS);
            return item.toUpperCase();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return item;
        }
    }
    
    /**
     * Benchmark para operações de busca
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
    
    @Benchmark
    public List<String> parallelSearch() {
        return testData.parallelStream()
            .filter(item -> item.contains("5"))
            .collect(Collectors.toList());
    }
    
    /**
     * Benchmark para agregações
     */
    @Benchmark
    public long sequentialCount() {
        return testData.stream()
            .filter(item -> item.length() > 5)
            .count();
    }
    
    @Benchmark
    public long parallelCount() {
        return testData.parallelStream()
            .filter(item -> item.length() > 5)
            .count();
    }
    
    /**
     * Main para executar benchmarks
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ConcurrencyBenchmark.class.getSimpleName())
            .forks(1)
            .build();
        
        new Runner(opt).run();
    }
}

// Made with Bob
