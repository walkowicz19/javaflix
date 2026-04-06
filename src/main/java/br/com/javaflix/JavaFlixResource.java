package br.com.javaflix;

import br.com.javaflix.service.ConteudoService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JavaFlixResource {

    @Inject
    ConteudoService conteudoService;
    
    @Inject
    PlataformaStreaming netflixClone;
    
    @Inject
    MeterRegistry meterRegistry;

    @GET
    @Path("/catalogo")
    @PermitAll
    @Timed(value = "javaflix.catalogo", description = "Tempo para listar catálogo")
    public List<Conteudo> getCatalogo() {
        meterRegistry.counter("javaflix.catalogo.requests").increment();
        try {
            return conteudoService.listarTodos();
        } catch (Exception e) {
            // Fallback para mock se PocketBase não estiver disponível
            return netflixClone.getCatalogo();
        }
    }

    @GET
    @Path("/buscar")
    @PermitAll
    @Timed(value = "javaflix.buscar", description = "Tempo para buscar conteúdo")
    public Response buscar(@QueryParam("q") String query) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            if (query == null || query.trim().isEmpty()) {
                meterRegistry.counter("javaflix.buscar.errors", "type", "bad_request").increment();
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"erro\": \"Missing search parameter\"}").build();
            }
            Conteudo c = conteudoService.buscarPorTitulo(query);
            meterRegistry.counter("javaflix.buscar.success").increment();
            return Response.ok(c).build();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ConteudoNaoEncontradoException || e.getMessage().contains("não encontrado")) {
                meterRegistry.counter("javaflix.buscar.errors", "type", "not_found").increment();
                return Response.status(Response.Status.NOT_FOUND).entity("{\"erro\": \"Conteúdo não encontrado\"}").build();
            }
            meterRegistry.counter("javaflix.buscar.errors", "type", "internal_error").increment();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"erro\": \"Internal server error\"}").build();
        } catch (Exception e) {
            meterRegistry.counter("javaflix.buscar.errors", "type", "internal_error").increment();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"erro\": \"Internal server error\"}").build();
        } finally {
            sample.stop(meterRegistry.timer("javaflix.buscar.duration"));
        }
    }

    @GET
    @Path("/recomendacoes")
    @RolesAllowed({"user", "admin", "premium"})
    @Timed(value = "javaflix.recomendacoes", description = "Tempo para gerar recomendações")
    public java.util.concurrent.CompletionStage<List<Conteudo>> getRecomendacoes() {
        meterRegistry.counter("javaflix.recomendacoes.requests").increment();
        long startTime = System.nanoTime();
        
        return netflixClone.obterRecomendacoesAsync()
            .whenComplete((result, error) -> {
                long duration = System.nanoTime() - startTime;
                meterRegistry.timer("javaflix.recomendacoes.async.duration")
                    .record(duration, TimeUnit.NANOSECONDS);
                
                if (error != null) {
                    meterRegistry.counter("javaflix.recomendacoes.errors").increment();
                } else {
                    meterRegistry.counter("javaflix.recomendacoes.success").increment();
                }
            });
    }

    @POST
    @Path("/transcodificar")
    @RolesAllowed({"admin"})
    @Timed(value = "javaflix.transcodificar", description = "Tempo para iniciar transcodificação")
    public Response transcodificar() {
        meterRegistry.counter("javaflix.transcodificar.requests").increment();
        
        if (!netflixClone.getCatalogo().isEmpty()) {
            Conteudo filmeSimulado = netflixClone.getCatalogo().get(0);
            netflixClone.iniciarTranscodificacaoOriginal(filmeSimulado);
            meterRegistry.counter("javaflix.transcodificar.success").increment();
            return Response.accepted("{\"mensagem\": \"Transcodificação CPU-bound em background disparada com sucesso.\"}").build();
        }
        meterRegistry.counter("javaflix.transcodificar.errors", "type", "empty_catalog").increment();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/notificacoes")
    @RolesAllowed({"admin"})
    @Timed(value = "javaflix.notificacoes", description = "Tempo para enviar notificações")
    public Response notificacoes() {
        meterRegistry.counter("javaflix.notificacoes.requests").increment();
        netflixClone.notificarUsuariosPush("Lançamento: O Poderoso Chefão 4");
        meterRegistry.counter("javaflix.notificacoes.success").increment();
        return Response.accepted("{\"mensagem\": \"Notificações enfileiradas I/O-bound para envio aos usuários.\"}").build();
    }
    
    /**
     * Endpoint para expor métricas customizadas
     */
    @GET
    @Path("/metrics/custom")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomMetrics() {
        return Response.ok()
            .entity("{\n" +
                "  \"info\": \"Métricas disponíveis em /q/metrics (Prometheus format)\",\n" +
                "  \"custom_metrics\": [\n" +
                "    \"javaflix_catalogo_requests_total\",\n" +
                "    \"javaflix_buscar_success_total\",\n" +
                "    \"javaflix_buscar_errors_total\",\n" +
                "    \"javaflix_recomendacoes_requests_total\",\n" +
                "    \"javaflix_transcodificar_requests_total\",\n" +
                "    \"javaflix_notificacoes_requests_total\",\n" +
                "    \"javaflix_catalogo_seconds\",\n" +
                "    \"javaflix_buscar_seconds\",\n" +
                "    \"javaflix_recomendacoes_seconds\"\n" +
                "  ]\n" +
                "}")
            .build();
    }
}
