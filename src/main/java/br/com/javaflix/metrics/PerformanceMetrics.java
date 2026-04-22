package br.com.javaflix.metrics;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class PerformanceMetrics {

    private static final Logger LOG = Logger.getLogger(PerformanceMetrics.class);

    @ConfigProperty(name = "javaflix.metrics.enabled", defaultValue = "true")
    boolean metricsEnabled;

    @ConfigProperty(name = "javaflix.metrics.log-slow-queries", defaultValue = "true")
    boolean logSlowQueries;

    @ConfigProperty(name = "javaflix.metrics.slow-query-threshold-ms", defaultValue = "500")
    long slowQueryThresholdMs;

    private final ConcurrentHashMap<String, AtomicLong> operationCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> operationTimes = new ConcurrentHashMap<>();

    public void recordOperation(String operationName, long durationMs) {
        if (!metricsEnabled) {
            return;
        }

        operationCounts.computeIfAbsent(operationName, key -> new AtomicLong()).incrementAndGet();
        operationTimes.computeIfAbsent(operationName, key -> new AtomicLong()).addAndGet(durationMs);

        if (logSlowQueries && durationMs > slowQueryThresholdMs) {
            LOG.warnf("Operação lenta detectada: %s levou %d ms", operationName, durationMs);
        } else {
            LOG.debugf("Métrica registrada: %s levou %d ms", operationName, durationMs);
        }
    }

    public long getOperationCount(String operationName) {
        return operationCounts.getOrDefault(operationName, new AtomicLong()).get();
    }

    public long getAverageTime(String operationName) {
        long count = getOperationCount(operationName);
        if (count == 0) {
            return 0;
        }

        long totalTime = operationTimes.getOrDefault(operationName, new AtomicLong()).get();
        return totalTime / count;
    }

    public void logMetrics() {
        if (!metricsEnabled) {
            return;
        }

        LOG.info("=== Métricas de Performance ===");
        operationCounts.forEach((operation, count) -> {
            long avgTime = getAverageTime(operation);
            LOG.infof("%s: %d execuções, tempo médio: %d ms", operation, count.get(), avgTime);
        });
    }
}

// Made with Bob
