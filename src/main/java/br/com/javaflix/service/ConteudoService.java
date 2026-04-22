package br.com.javaflix.service;

import br.com.javaflix.Conteudo;
import br.com.javaflix.ConteudoNaoEncontradoException;
import br.com.javaflix.Filme;
import br.com.javaflix.Serie;
import br.com.javaflix.client.PocketBaseClient;
import br.com.javaflix.client.dto.ConteudoRecord;
import br.com.javaflix.client.dto.ConteudoRequest;
import br.com.javaflix.metrics.PerformanceMetrics;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Service layer para gerenciamento de conteúdos com integração ao PocketBase.
 * Implementa operações CRUD e busca com suporte a concorrência.
 */
@ApplicationScoped
public class ConteudoService {

    private static final Logger LOG = Logger.getLogger(ConteudoService.class);

    @Inject
    @RestClient
    PocketBaseClient pocketBaseClient;

    @Inject
    @Named("javaflixExecutor")
    ExecutorService executorService;

    @Inject
    PerformanceMetrics performanceMetrics;

    /**
     * Retorna estatísticas básicas do pool de threads.
     */
    public String getPoolStats() {
        return String.format("Executor em uso: %s", executorService.getClass().getSimpleName());
    }

    /**
     * Lista todos os conteúdos do catálogo.
     *
     * @return Lista de conteúdos
     */
    public List<Conteudo> listarTodos() {
        long inicio = System.currentTimeMillis();
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
        } finally {
            registrarMetricas("listarTodos", inicio);
        }
    }

    /**
     * Lista todos os conteúdos de forma assíncrona.
     *
     * @return CompletableFuture com lista de conteúdos
     */
    public CompletableFuture<List<Conteudo>> listarTodosAsync() {
        long inicio = System.currentTimeMillis();
        LOG.info("Listando conteúdos de forma assíncrona");
        LOG.debug(getPoolStats());

        return CompletableFuture.supplyAsync(this::listarTodos, executorService)
            .whenComplete((resultado, erro) -> registrarMetricas("listarTodosAsync", inicio));
    }

    /**
     * Busca um conteúdo por título (case-insensitive).
     *
     * @param titulo Título do conteúdo
     * @return Conteúdo encontrado
     */
    public Conteudo buscarPorTitulo(String titulo) {
        long inicio = System.currentTimeMillis();
        LOG.infof("Buscando conteúdo por título: %s", titulo);

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        try {
            String filter = String.format("titulo~'%s'", titulo.trim());
            var response = pocketBaseClient.getConteudos(1, 1, filter, null);

            if (response.items.isEmpty()) {
                LOG.warnf("Conteúdo não encontrado: %s", titulo);
                throw new RuntimeException(new ConteudoNaoEncontradoException("Conteúdo não encontrado: " + titulo));
            }

            LOG.infof("Conteúdo encontrado: %s", response.items.get(0).titulo);
            return mapToConteudo(response.items.get(0));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ConteudoNaoEncontradoException) {
                throw e;
            }
            LOG.error("Erro ao buscar conteúdo por título", e);
            throw new RuntimeException("Erro ao buscar conteúdo", e);
        } catch (Exception e) {
            LOG.error("Erro ao buscar conteúdo por título", e);
            throw new RuntimeException("Erro ao buscar conteúdo", e);
        } finally {
            registrarMetricas("buscarPorTitulo", inicio);
        }
    }

    /**
     * Filtra conteúdos por gênero.
     *
     * @param genero Gênero para filtrar
     * @return Lista de conteúdos do gênero
     */
    public List<Conteudo> filtrarPorGenero(String genero) {
        long inicio = System.currentTimeMillis();
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
        } finally {
            registrarMetricas("filtrarPorGenero", inicio);
        }
    }

    /**
     * Filtra conteúdos por gênero de forma paralela.
     *
     * @param generos Lista de gêneros
     * @return Lista de conteúdos dos gêneros especificados
     */
    public List<Conteudo> filtrarPorGenerosParalelo(List<String> generos) {
        long inicio = System.currentTimeMillis();
        LOG.infof("Filtrando conteúdos por %d gêneros em paralelo", generos.size());

        try {
            List<CompletableFuture<List<Conteudo>>> futures = generos.stream()
                .map(genero -> CompletableFuture.supplyAsync(() -> filtrarPorGenero(genero), executorService))
                .collect(Collectors.toList());

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .flatMap(future -> future.join().stream())
                    .distinct()
                    .collect(Collectors.toList()))
                .join();
        } finally {
            registrarMetricas("filtrarPorGenerosParalelo", inicio);
        }
    }

    /**
     * Cria um novo conteúdo no PocketBase.
     *
     * @param conteudo Conteúdo a ser criado
     * @return Conteúdo criado com ID
     */
    public Conteudo criar(Conteudo conteudo) {
        long inicio = System.currentTimeMillis();
        LOG.infof("Criando novo conteúdo: %s", conteudo.getTitulo());

        try {
            ConteudoRequest request = mapToRequest(conteudo);
            ConteudoRecord record = pocketBaseClient.createConteudo(request);

            LOG.infof("Conteúdo criado com ID: %s", record.id);
            return mapToConteudo(record);
        } catch (Exception e) {
            LOG.error("Erro ao criar conteúdo", e);
            throw new RuntimeException("Erro ao criar conteúdo no banco de dados", e);
        } finally {
            registrarMetricas("criar", inicio);
        }
    }

    /**
     * Atualiza um conteúdo existente.
     *
     * @param id ID do conteúdo
     * @param conteudo Dados atualizados
     * @return Conteúdo atualizado
     */
    public Conteudo atualizar(String id, Conteudo conteudo) {
        long inicio = System.currentTimeMillis();
        LOG.infof("Atualizando conteúdo ID: %s", id);

        try {
            ConteudoRequest request = mapToRequest(conteudo);
            ConteudoRecord record = pocketBaseClient.updateConteudo(id, request);

            LOG.info("Conteúdo atualizado com sucesso");
            return mapToConteudo(record);
        } catch (Exception e) {
            LOG.error("Erro ao atualizar conteúdo", e);
            throw new RuntimeException("Erro ao atualizar conteúdo", e);
        } finally {
            registrarMetricas("atualizar", inicio);
        }
    }

    /**
     * Remove um conteúdo.
     *
     * @param id ID do conteúdo
     */
    public void remover(String id) {
        long inicio = System.currentTimeMillis();
        LOG.infof("Removendo conteúdo ID: %s", id);

        try {
            pocketBaseClient.deleteConteudo(id);
            LOG.info("Conteúdo removido com sucesso");
        } catch (Exception e) {
            LOG.error("Erro ao remover conteúdo", e);
            throw new RuntimeException("Erro ao remover conteúdo", e);
        } finally {
            registrarMetricas("remover", inicio);
        }
    }

    /**
     * Busca múltiplos conteúdos por título em paralelo.
     *
     * @param titulos Lista de títulos
     * @return CompletableFuture com lista de conteúdos
     */
    public CompletableFuture<List<Conteudo>> buscarParalelo(List<String> titulos) {
        long inicio = System.currentTimeMillis();
        LOG.infof("Buscando %d conteúdos em paralelo", titulos.size());
        LOG.debug(getPoolStats());

        List<CompletableFuture<Conteudo>> futures = titulos.stream()
            .map(titulo -> CompletableFuture.supplyAsync(() -> {
                try {
                    return buscarPorTitulo(titulo);
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof ConteudoNaoEncontradoException) {
                        LOG.warnf("Conteúdo não encontrado na busca paralela: %s", titulo);
                        return null;
                    }
                    throw e;
                }
            }, executorService))
            .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(conteudo -> conteudo != null)
                .collect(Collectors.toList()))
            .whenComplete((resultado, erro) -> {
                registrarMetricas("buscarParalelo", inicio);
                performanceMetrics.logMetrics();
            });
    }

    /**
     * Registra métricas e logging de performance para a operação informada.
     */
    private void registrarMetricas(String operacao, long inicio) {
        long duracao = System.currentTimeMillis() - inicio;
        performanceMetrics.recordOperation(operacao, duracao);
        LOG.debugf("Operação %s finalizada em %d ms", operacao, duracao);
    }

    /**
     * Mapeia ConteudoRecord do PocketBase para objeto de domínio Conteudo.
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
     * Mapeia objeto de domínio Conteudo para ConteudoRequest do PocketBase.
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
