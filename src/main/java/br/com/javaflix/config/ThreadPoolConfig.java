package br.com.javaflix.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuração centralizada do pool de threads utilizado nas operações assíncronas.
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

    private ExecutorService executorService;

    @Produces
    @Named("javaflixExecutor")
    @ApplicationScoped
    public ExecutorService createExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            LOG.infof(
                "Criando executor do JavaFlix com core-size=%d, max-size=%d, queue-capacity=%d",
                corePoolSize,
                maxPoolSize,
                queueCapacity
            );
            executorService = Executors.newFixedThreadPool(corePoolSize);
        }
        return executorService;
    }

    @PreDestroy
    void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            LOG.info("Encerrando executor do JavaFlix");
            executorService.shutdown();
        }
    }
}

// Made with Bob
