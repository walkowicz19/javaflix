package br.com.javaflix.resource;

import br.com.javaflix.client.PocketBaseClient;
import br.com.javaflix.client.dto.AuthRequest;
import br.com.javaflix.client.dto.AuthResponse;
import br.com.javaflix.client.dto.UserRequest;
import br.com.javaflix.client.dto.UserRecord;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * Resource para autenticação e gerenciamento de usuários
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    private static final Logger LOG = Logger.getLogger(AuthResource.class);
    
    @Inject
    @RestClient
    PocketBaseClient pocketBaseClient;
    
    /**
     * Endpoint de login
     * 
     * POST /api/auth/login
     * Body: { "identity": "email@example.com", "password": "senha123" }
     * 
     * @param request Credenciais de login
     * @return Token JWT e dados do usuário
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(AuthRequest request) {
        LOG.infof("Tentativa de login para: %s", request.identity);
        
        // Validação básica
        if (request.identity == null || request.identity.trim().isEmpty()) {
            return Response.status(400)
                .entity(Map.of("error", "Email/username é obrigatório"))
                .build();
        }
        
        if (request.password == null || request.password.trim().isEmpty()) {
            return Response.status(400)
                .entity(Map.of("error", "Senha é obrigatória"))
                .build();
        }
        
        try {
            AuthResponse authResponse = pocketBaseClient.authenticate(request);
            LOG.infof("Login bem-sucedido para: %s", request.identity);
            
            return Response.ok(Map.of(
                "token", authResponse.token,
                "user", Map.of(
                    "id", authResponse.record.id,
                    "username", authResponse.record.username,
                    "email", authResponse.record.email,
                    "name", authResponse.record.name != null ? authResponse.record.name : "",
                    "idade", authResponse.record.idade,
                    "tipoAssinatura", authResponse.record.tipoAssinatura
                )
            )).build();
            
        } catch (WebApplicationException e) {
            LOG.warnf("Falha no login para %s: %s", request.identity, e.getMessage());
            
            if (e.getResponse().getStatus() == 400) {
                return Response.status(401)
                    .entity(Map.of("error", "Credenciais inválidas"))
                    .build();
            }
            
            return Response.status(500)
                .entity(Map.of("error", "Erro ao processar login"))
                .build();
                
        } catch (Exception e) {
            LOG.error("Erro inesperado no login", e);
            return Response.status(500)
                .entity(Map.of("error", "Erro interno do servidor"))
                .build();
        }
    }
    
    /**
     * Endpoint de registro
     * 
     * POST /api/auth/register
     * Body: {
     *   "username": "usuario",
     *   "email": "email@example.com",
     *   "password": "senha123",
     *   "passwordConfirm": "senha123",
     *   "name": "Nome Completo",
     *   "idade": 25,
     *   "tipoAssinatura": "basico"
     * }
     * 
     * @param request Dados do novo usuário
     * @return Usuário criado
     */
    @POST
    @Path("/register")
    @PermitAll
    public Response register(UserRequest request) {
        LOG.infof("Tentativa de registro para: %s", request.email);
        
        // Validações
        if (request.username == null || request.username.trim().isEmpty()) {
            return Response.status(400)
                .entity(Map.of("error", "Username é obrigatório"))
                .build();
        }
        
        if (request.email == null || request.email.trim().isEmpty()) {
            return Response.status(400)
                .entity(Map.of("error", "Email é obrigatório"))
                .build();
        }
        
        if (request.password == null || request.password.length() < 8) {
            return Response.status(400)
                .entity(Map.of("error", "Senha deve ter no mínimo 8 caracteres"))
                .build();
        }
        
        if (!request.password.equals(request.passwordConfirm)) {
            return Response.status(400)
                .entity(Map.of("error", "Senhas não conferem"))
                .build();
        }
        
        if (request.idade < 13) {
            return Response.status(400)
                .entity(Map.of("error", "Idade mínima é 13 anos"))
                .build();
        }
        
        // Definir tipo de assinatura padrão se não fornecido
        if (request.tipoAssinatura == null || request.tipoAssinatura.trim().isEmpty()) {
            request.tipoAssinatura = "basico";
        }
        
        try {
            UserRecord userRecord = pocketBaseClient.createUser(request);
            LOG.infof("Usuário registrado com sucesso: %s", userRecord.email);
            
            return Response.status(201)
                .entity(Map.of(
                    "message", "Usuário criado com sucesso",
                    "user", Map.of(
                        "id", userRecord.id,
                        "username", userRecord.username,
                        "email", userRecord.email,
                        "name", userRecord.name != null ? userRecord.name : "",
                        "idade", userRecord.idade,
                        "tipoAssinatura", userRecord.tipoAssinatura
                    )
                ))
                .build();
                
        } catch (WebApplicationException e) {
            LOG.warnf("Falha no registro para %s: %s", request.email, e.getMessage());
            
            if (e.getResponse().getStatus() == 400) {
                return Response.status(400)
                    .entity(Map.of("error", "Email ou username já existe"))
                    .build();
            }
            
            return Response.status(500)
                .entity(Map.of("error", "Erro ao criar usuário"))
                .build();
                
        } catch (Exception e) {
            LOG.error("Erro inesperado no registro", e);
            return Response.status(500)
                .entity(Map.of("error", "Erro interno do servidor"))
                .build();
        }
    }
    
    /**
     * Endpoint para verificar se o token é válido
     * 
     * GET /api/auth/verify
     * Header: Authorization: Bearer TOKEN
     * 
     * @return Status do token
     */
    @GET
    @Path("/verify")
    @RolesAllowed({"user", "admin", "premium"})
    public Response verifyToken(@Context SecurityContext securityContext) {
        // Este endpoint é protegido pelo JWT
        // Se chegar aqui, o token é válido
        String username = securityContext.getUserPrincipal() != null
            ? securityContext.getUserPrincipal().getName()
            : "unknown";
            
        return Response.ok(Map.of(
            "valid", true,
            "message", "Token válido",
            "user", username,
            "roles", securityContext.isUserInRole("admin") ? "admin" : "user"
        )).build();
    }
    
    /**
     * Endpoint de health check
     * 
     * GET /api/auth/health
     * 
     * @return Status do serviço de autenticação
     */
    @GET
    @Path("/health")
    @PermitAll
    public Response health() {
        try {
            // Tenta fazer uma chamada simples ao PocketBase
            return Response.ok(Map.of(
                "status", "healthy",
                "service", "authentication",
                "pocketbase", "connected"
            )).build();
        } catch (Exception e) {
            LOG.error("Health check falhou", e);
            return Response.status(503)
                .entity(Map.of(
                    "status", "unhealthy",
                    "service", "authentication",
                    "error", "PocketBase não disponível"
                ))
                .build();
        }
    }
}

// Made with Bob
