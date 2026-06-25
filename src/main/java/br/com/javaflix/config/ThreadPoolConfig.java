package br.com.javaflix.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Configuração centralizada do pool de threads utilizado nas operações assíncronas.
 *
 * Usa ThreadPoolExecutor explícito para que core-size, max-size e queue-capacity
 * sejam todos honrados — ao contrário de Executors.newFixedThreadPool que ignora
 * max-size e queue-capacity.
 *
 * Comportamento:
 *   - Até corePoolSize threads ficam ativas permanentemente.
 *   - Quando a fila (queueCapacity) enche, novas threads são criadas até maxPoolSize.
 *   - Threads acima do core ficam ociosas por 60 s antes de serem encerradas.
 *   - Se pool + fila estiverem cheios, a task é executada na thread do chamador
 *     (CallerRunsPolicy) em vez de ser rejeitada.
 */
@ApplicationScoped
public class ThreadPoolConfig {

    private static final Logger LOG = Logger.getLogger(ThreadPoolConfig.class);

    @ConfigProperty(name = "javaflix.threadpool.core-size", defaultValue = "10")
    int corePoolSize;

    @ConfigProperty(name = "javaflix.threadpool.max-size", defaultValue = "20")
    int maxPoolSize;

    @ConfigProperty(name = "javaflix.threadpool.queue-capacity", defaultValue = "100")
    int queueCapacity;

    private ThreadPoolExecutor executorService;

    @Produces
    @Named("javaflixExecutor")
    @ApplicationScoped
    public ExecutorService createExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            LOG.infof(
                "Criando ThreadPoolExecutor JavaFlix — core=%d, max=%d, queue=%d",
                corePoolSize, maxPoolSize, queueCapacity
            );
            executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                r -> {
                    Thread t = new Thread(r, "javaflix-worker-" + System.nanoTime());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
            );
            // Mantém threads de core vivas mesmo ociosas
            executorService.allowCoreThreadTimeOut(false);
            LOG.infof(
                "ThreadPoolExecutor iniciado — threads ativas: %d / max: %d",
                executorService.getActiveCount(), executorService.getMaximumPoolSize()
            );
        }
        return executorService;
    }

    /**
     * Expõe o executor concreto para logging rico de diagnóstico.
     */
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return executorService;
    }

    @PreDestroy
    void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            LOG.infof(
                "Encerrando executor JavaFlix — tasks completadas: %d, na fila: %d",
                executorService.getCompletedTaskCount(),
                executorService.getQueue().size()
            );
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    LOG.warn("Executor forçado a encerrar após timeout de 10 s");
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Made with Bob
