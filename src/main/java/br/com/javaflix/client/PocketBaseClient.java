package br.com.javaflix.client;

import br.com.javaflix.client.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Cliente REST para comunicação com PocketBase
 * Documentação da API: https://pocketbase.io/docs/api-records/
 */
@Path("/collections")
@RegisterRestClient(configKey = "br.com.javaflix.client.PocketBaseClient")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PocketBaseClient {
    
    /**
     * Lista todos os conteúdos (filmes e séries)
     * 
     * @param page Número da página (padrão: 1)
     * @param perPage Itens por página (padrão: 30, máx: 500)
     * @param filter Filtro no formato PocketBase (ex: "genero='Ação'")
     * @param sort Ordenação (ex: "-created" para decrescente)
     * @return Lista paginada de conteúdos
     */
    @GET
    @Path("/conteudos/records")
    ConteudoListResponse getConteudos(
        @QueryParam("page") @DefaultValue("1") int page,
        @QueryParam("perPage") @DefaultValue("100") int perPage,
        @QueryParam("filter") String filter,
        @QueryParam("sort") String sort
    );
    
    /**
     * Busca um conteúdo específico por ID
     * 
     * @param id ID do conteúdo no PocketBase
     * @return Dados do conteúdo
     */
    @GET
    @Path("/conteudos/records/{id}")
    ConteudoRecord getConteudo(@PathParam("id") String id);
    
    /**
     * Cria um novo conteúdo
     * 
     * @param request Dados do conteúdo a ser criado
     * @return Conteúdo criado com ID gerado
     */
    @POST
    @Path("/conteudos/records")
    ConteudoRecord createConteudo(ConteudoRequest request);
    
    /**
     * Atualiza um conteúdo existente
     * 
     * @param id ID do conteúdo
     * @param request Dados atualizados
     * @return Conteúdo atualizado
     */
    @PATCH
    @Path("/conteudos/records/{id}")
    ConteudoRecord updateConteudo(
        @PathParam("id") String id,
        ConteudoRequest request
    );
    
    /**
     * Remove um conteúdo
     * 
     * @param id ID do conteúdo a ser removido
     */
    @DELETE
    @Path("/conteudos/records/{id}")
    void deleteConteudo(@PathParam("id") String id);
    
    /**
     * Autentica um usuário
     * 
     * @param request Credenciais de login
     * @return Token JWT e dados do usuário
     */
    @POST
    @Path("/users/auth-with-password")
    AuthResponse authenticate(AuthRequest request);
    
    /**
     * Registra um novo usuário
     * 
     * @param request Dados do novo usuário
     * @return Usuário criado
     */
    @POST
    @Path("/users/records")
    UserRecord createUser(UserRequest request);
    
    /**
     * Busca dados de um usuário
     * 
     * @param id ID do usuário
     * @return Dados do usuário
     */
    @GET
    @Path("/users/records/{id}")
    UserRecord getUser(@PathParam("id") String id);
    
    /**
     * Lista avaliações de um conteúdo
     * 
     * @param conteudoId ID do conteúdo
     * @return Lista de avaliações
     */
    @GET
    @Path("/avaliacoes/records")
    AvaliacaoListResponse getAvaliacoes(
        @QueryParam("filter") String filter,
        @QueryParam("expand") String expand
    );
    
    /**
     * Cria uma nova avaliação
     * 
     * @param request Dados da avaliação
     * @return Avaliação criada
     */
    @POST
    @Path("/avaliacoes/records")
    AvaliacaoRecord createAvaliacao(AvaliacaoRequest request);
}

// Made with Bob
