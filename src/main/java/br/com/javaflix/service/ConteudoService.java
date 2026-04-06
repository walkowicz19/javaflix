package br.com.javaflix.service;

import br.com.javaflix.Conteudo;
import br.com.javaflix.Filme;
import br.com.javaflix.Serie;
import br.com.javaflix.ConteudoNaoEncontradoException;
import br.com.javaflix.client.PocketBaseClient;
import br.com.javaflix.client.dto.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service layer para gerenciamento de conteúdos com integração ao PocketBase.
 * Implementa operações CRUD e busca com suporte a concorrência.
 */
@ApplicationScoped
public class ConteudoService {
    
    private static final Logger LOG = Logger.getLogger(ConteudoService.class);
    
    // Pool de threads configurável para operações assíncronas
    private ExecutorService executorService;
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L;
    
    @Inject
    @RestClient
    PocketBaseClient pocketBaseClient;
    
    /**
     * Inicializa o pool de threads customizado
     */
    @PostConstruct
    public void init() {
        LOG.info("Inicializando pool de threads customizado");
        executorService = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactory() {
                private int counter = 0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("javaflix-worker-" + counter++);
                    thread.setDaemon(false);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        LOG.infof("Pool de threads criado: core=%d, max=%d, keepAlive=%ds",
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME);
    }
    
    /**
     * Finaliza o pool de threads de forma ordenada
     */
    @PreDestroy
    public void destroy() {
        LOG.info("Finalizando pool de threads");
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    LOG.warn("Pool de threads não finalizou no tempo esperado, forçando shutdown");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOG.error("Interrompido durante shutdown do pool", e);
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Retorna estatísticas do pool de threads
     */
    public String getPoolStats() {
        if (executorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) executorService;
            return String.format(
                "Pool Stats - Active: %d, Completed: %d, Queue: %d, Pool Size: %d",
                tpe.getActiveCount(),
                tpe.getCompletedTaskCount(),
                tpe.getQueue().size(),
                tpe.getPoolSize()
            );
        }
        return "Pool stats not available";
    }
    
    /**
     * Lista todos os conteúdos do catálogo
     * 
     * @return Lista de conteúdos
     */
    public List<Conteudo> listarTodos() {
        LOG.info("Listando todos os conteúdos do PocketBase");
        
        try {
            var response = pocketBaseClient.getConteudos(1, 500, null, "-created");
            LOG.infof("Encontrados %d conteúdos", response.totalItems);
            
            return response.items.stream()
                .map(this::mapToConteudo)
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Erro ao listar conteúdos", e);
            throw new RuntimeException("Erro ao buscar conteúdos do banco de dados", e);
        }
    }
    
    /**
     * Lista todos os conteúdos de forma assíncrona
     * 
     * @return CompletableFuture com lista de conteúdos
     */
    public CompletableFuture<List<Conteudo>> listarTodosAsync() {
        LOG.info("Listando conteúdos de forma assíncrona");
        LOG.debug(getPoolStats());
        return CompletableFuture.supplyAsync(this::listarTodos, executorService);
    }
    
    /**
     * Busca um conteúdo por título (case-insensitive)
     * 
     * @param titulo Título do conteúdo
     * @return Conteúdo encontrado
     * @throws ConteudoNaoEncontradoException se não encontrar
     */
    public Conteudo buscarPorTitulo(String titulo) {
        LOG.infof("Buscando conteúdo por título: %s", titulo);
        
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }
        
        try {
            // Filtro do PocketBase para busca case-insensitive
            String filter = String.format("titulo~'%s'", titulo.trim());
            var response = pocketBaseClient.getConteudos(1, 1, filter, null);
            
            if (response.items.isEmpty()) {
                LOG.warnf("Conteúdo não encontrado: %s", titulo);
                throw new ConteudoNaoEncontradoException("Conteúdo não encontrado: " + titulo);
            }
            
            LOG.infof("Conteúdo encontrado: %s", response.items.get(0).titulo);
            return mapToConteudo(response.items.get(0));
        } catch (ConteudoNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Erro ao buscar conteúdo por título", e);
            throw new RuntimeException("Erro ao buscar conteúdo", e);
        }
    }
    
    /**
     * Filtra conteúdos por gênero
     * 
     * @param genero Gênero para filtrar
     * @return Lista de conteúdos do gênero
     */
    public List<Conteudo> filtrarPorGenero(String genero) {
        LOG.infof("Filtrando conteúdos por gênero: %s", genero);
        
        if (genero == null || genero.trim().isEmpty()) {
            return listarTodos();
        }
        
        try {
            String filter = String.format("genero~'%s'", genero.trim());
            var response = pocketBaseClient.getConteudos(1, 500, filter, "-created");
            
            LOG.infof("Encontrados %d conteúdos do gênero %s", response.items.size(), genero);
            
            return response.items.stream()
                .map(this::mapToConteudo)
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Erro ao filtrar por gênero", e);
            throw new RuntimeException("Erro ao filtrar conteúdos", e);
        }
    }
    
    /**
     * Filtra conteúdos por gênero de forma paralela (para múltiplos gêneros)
     * 
     * @param generos Lista de gêneros
     * @return Lista de conteúdos dos gêneros especificados
     */
    public List<Conteudo> filtrarPorGenerosParalelo(List<String> generos) {
        LOG.infof("Filtrando conteúdos por %d gêneros em paralelo", generos.size());
        
        return generos.parallelStream()
            .flatMap(genero -> filtrarPorGenero(genero).stream())
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Cria um novo conteúdo no PocketBase
     * 
     * @param conteudo Conteúdo a ser criado
     * @return Conteúdo criado com ID
     */
    public Conteudo criar(Conteudo conteudo) {
        LOG.infof("Criando novo conteúdo: %s", conteudo.getTitulo());
        
        try {
            ConteudoRequest request = mapToRequest(conteudo);
            ConteudoRecord record = pocketBaseClient.createConteudo(request);
            
            LOG.infof("Conteúdo criado com ID: %s", record.id);
            return mapToConteudo(record);
        } catch (Exception e) {
            LOG.error("Erro ao criar conteúdo", e);
            throw new RuntimeException("Erro ao criar conteúdo no banco de dados", e);
        }
    }
    
    /**
     * Atualiza um conteúdo existente
     * 
     * @param id ID do conteúdo
     * @param conteudo Dados atualizados
     * @return Conteúdo atualizado
     */
    public Conteudo atualizar(String id, Conteudo conteudo) {
        LOG.infof("Atualizando conteúdo ID: %s", id);
        
        try {
            ConteudoRequest request = mapToRequest(conteudo);
            ConteudoRecord record = pocketBaseClient.updateConteudo(id, request);
            
            LOG.info("Conteúdo atualizado com sucesso");
            return mapToConteudo(record);
        } catch (Exception e) {
            LOG.error("Erro ao atualizar conteúdo", e);
            throw new RuntimeException("Erro ao atualizar conteúdo", e);
        }
    }
    
    /**
     * Remove um conteúdo
     * 
     * @param id ID do conteúdo
     */
    public void remover(String id) {
        LOG.infof("Removendo conteúdo ID: %s", id);
        
        try {
            pocketBaseClient.deleteConteudo(id);
            LOG.info("Conteúdo removido com sucesso");
        } catch (Exception e) {
            LOG.error("Erro ao remover conteúdo", e);
            throw new RuntimeException("Erro ao remover conteúdo", e);
        }
    }
    
    /**
     * Busca múltiplos conteúdos por título em paralelo
     * 
     * @param titulos Lista de títulos
     * @return CompletableFuture com lista de conteúdos
     */
    public CompletableFuture<List<Conteudo>> buscarParalelo(List<String> titulos) {
        LOG.infof("Buscando %d conteúdos em paralelo", titulos.size());
        LOG.debug(getPoolStats());
        
        List<CompletableFuture<Conteudo>> futures = titulos.stream()
            .map(titulo -> CompletableFuture.supplyAsync(() -> {
                try {
                    return buscarPorTitulo(titulo);
                } catch (ConteudoNaoEncontradoException e) {
                    LOG.warnf("Conteúdo não encontrado na busca paralela: %s", titulo);
                    return null;
                }
            }, executorService))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(c -> c != null)
                .collect(Collectors.toList())
            );
    }
    
    /**
     * Mapeia ConteudoRecord do PocketBase para objeto de domínio Conteudo
     */
    private Conteudo mapToConteudo(ConteudoRecord record) {
        if ("filme".equalsIgnoreCase(record.tipo)) {
            Filme filme = new Filme(
                record.titulo,
                record.genero,
                record.classificacaoEtaria,
                record.duracaoMinutos != null ? record.duracaoMinutos : 0,
                record.diretor != null ? record.diretor : "Desconhecido"
            );
            
            // TODO: Restaurar avaliações quando método avaliar() for implementado
            // if (record.avaliacoes != null) {
            //     record.avaliacoes.forEach(filme::avaliar);
            // }
            
            return filme;
        } else {
            Serie serie = new Serie(
                record.titulo,
                record.genero,
                record.classificacaoEtaria,
                record.temporadas != null ? record.temporadas : 1,
                record.episodiosPorTemporada != null ? record.episodiosPorTemporada : 10,
                record.duracaoMediaEpisodio != null ? record.duracaoMediaEpisodio : 45
            );
            
            // TODO: Restaurar avaliações quando método avaliar() for implementado
            // if (record.avaliacoes != null) {
            //     record.avaliacoes.forEach(serie::avaliar);
            // }
            
            return serie;
        }
    }
    
    /**
     * Mapeia objeto de domínio Conteudo para ConteudoRequest do PocketBase
     */
    private ConteudoRequest mapToRequest(Conteudo conteudo) {
        ConteudoRequest request = new ConteudoRequest();
        // TODO: Implementar getters nas classes Conteudo, Filme e Serie
        // request.titulo = conteudo.getTitulo();
        // request.genero = conteudo.getGenero();
        // request.classificacaoEtaria = conteudo.getClassificacaoEtaria();
        // request.mediaAvaliacoes = conteudo.obterMediaAvaliacoes();
        
        // Por enquanto, retorna request vazio
        request.tipo = conteudo instanceof Filme ? "filme" : "serie";
        
        // if (conteudo instanceof Filme) {
        //     Filme filme = (Filme) conteudo;
        //     request.duracaoMinutos = filme.getDuracaoMinutos();
        //     request.diretor = filme.getDiretor();
        // } else if (conteudo instanceof Serie) {
        //     Serie serie = (Serie) conteudo;
        //     request.temporadas = serie.getTemporadas();
        //     request.episodiosPorTemporada = serie.getEpisodiosPorTemporada();
        //     request.duracaoMediaEpisodio = serie.getDuracaoMediaEpisodioMinutos();
        // }
        
        return request;
    }
}

// Made with Bob
