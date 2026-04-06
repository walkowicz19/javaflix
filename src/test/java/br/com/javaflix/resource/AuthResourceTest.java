package br.com.javaflix.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração para AuthResource
 */
@QuarkusTest
class AuthResourceTest {
    
    @Test
    @DisplayName("Deve retornar erro 400 quando login sem email")
    void testLoginSemEmail() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("password", "senha123"))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400)
            .body("error", equalTo("Email/username é obrigatório"));
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando login sem senha")
    void testLoginSemSenha() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("identity", "teste@example.com"))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400)
            .body("error", equalTo("Senha é obrigatória"));
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando registro sem username")
    void testRegistroSemUsername() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "email", "teste@example.com",
                "password", "senha12345",
                "passwordConfirm", "senha12345",
                "idade", 25
            ))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body("error", equalTo("Username é obrigatório"));
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando senhas não conferem")
    void testRegistroSenhasNaoConferem() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", "testuser",
                "email", "teste@example.com",
                "password", "senha12345",
                "passwordConfirm", "senha54321",
                "idade", 25
            ))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body("error", equalTo("Senhas não conferem"));
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando senha muito curta")
    void testRegistroSenhaCurta() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", "testuser",
                "email", "teste@example.com",
                "password", "123",
                "passwordConfirm", "123",
                "idade", 25
            ))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body("error", equalTo("Senha deve ter no mínimo 8 caracteres"));
    }
    
    @Test
    @DisplayName("Deve retornar erro 400 quando idade menor que 13")
    void testRegistroIdadeMinima() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", "testuser",
                "email", "teste@example.com",
                "password", "senha12345",
                "passwordConfirm", "senha12345",
                "idade", 10
            ))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body("error", equalTo("Idade mínima é 13 anos"));
    }
    
    @Test
    @DisplayName("Health check deve retornar status")
    void testHealthCheck() {
        given()
        .when()
            .get("/api/auth/health")
        .then()
            .statusCode(anyOf(is(200), is(503)))
            .body("service", equalTo("authentication"));
    }
}

// Made with Bob
