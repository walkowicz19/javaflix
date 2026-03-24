package br.com.javaflix;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JavaFlixResource {

    @Inject
    PlataformaStreaming netflixClone;

    @GET
    @Path("/catalogo")
    public List<Conteudo> getCatalogo() {
        return netflixClone.getCatalogo();
    }

    @GET
    @Path("/buscar")
    public Response buscar(@QueryParam("q") String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"erro\": \"Missing search parameter\"}").build();
            }
            Conteudo c = netflixClone.buscar(query);
            return Response.ok(c).build();
        } catch (ConteudoNaoEncontradoException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"erro\": \"Conteúdo não encontrado\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"erro\": \"Internal server error\"}").build();
        }
    }

    @GET
    @Path("/recomendacoes")
    public java.util.concurrent.CompletionStage<List<Conteudo>> getRecomendacoes() {
        // Quarkus RESTEasy Reactive converte CompletableFuture assincronamente pra JSON de forma nativa!
        return netflixClone.obterRecomendacoesAsync();
    }

    @POST
    @Path("/transcodificar")
    public Response transcodificar() {
        if (!netflixClone.getCatalogo().isEmpty()) {
            Conteudo filmeSimulado = netflixClone.getCatalogo().get(0);
            netflixClone.iniciarTranscodificacaoOriginal(filmeSimulado);
            return Response.accepted("{\"mensagem\": \"Transcodificação CPU-bound em background disparada com sucesso.\"}").build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/notificacoes")
    public Response notificacoes() {
        netflixClone.notificarUsuariosPush("Lançamento: O Poderoso Chefão 4");
        return Response.accepted("{\"mensagem\": \"Notificações enfileiradas I/O-bound para envio aos usuários.\"}").build();
    }
}
