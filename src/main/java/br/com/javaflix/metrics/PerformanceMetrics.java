package br.com.javaflix.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Fachada de metricas de performance.
 * Delegada ao Micrometer/Prometheus — elimina duplicacao com contadores manuais.
 * Todas as metricas ficam em /q/metrics (formato Prometheus).
 */
@ApplicationScoped
public class PerformanceMetrics {

    private static final Logger LOG = Logger.getLogger(PerformanceMetrics.class);

    @Inject
    MeterRegistry meterRegistry;

    @ConfigProperty(name = "javaflix.metrics.slow-query-threshold-ms", defaultValue = "500")
    long slowQueryThresholdMs;

    public void recordOperation(String operationName, long durationMs) {
        Timer.builder("javaflix.service." + operationName)
            .description("Tempo de execucao da operacao " + operationName)
            .tag("operation", operationName)
            .register(meterRegistry)
            .record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);

        if (durationMs > slowQueryThresholdMs) {
            LOG.warnf("Operacao lenta: %s levou %d ms (threshold: %d ms)",
                operationName, durationMs, slowQueryThresholdMs);
        } else {
            LOG.debugf("Operacao %s concluida em %d ms", operationName, durationMs);
        }
    }

    public long getOperationCount(String operationName) {
        Timer timer = meterRegistry.find("javaflix.service." + operationName).timer();
        return timer != null ? (long) timer.count() : 0L;
    }

    public long getAverageTime(String operationName) {
        Timer timer = meterRegistry.find("javaflix.service." + operationName).timer();
        if (timer == null || timer.count() == 0) return 0L;
        return (long) timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void logMetrics() {
        LOG.info("=== Metricas de Performance (via Micrometer) ===");
        meterRegistry.getMeters().stream()
            .filter(m -> m.getId().getName().startsWith("javaflix.service."))
            .forEach(m -> LOG.infof("  %s: %s", m.getId().getName(), m.measure()));
    }
}

// Made with Bob