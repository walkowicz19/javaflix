package br.com.javaflix.service;

import br.com.javaflix.Conteudo;
import br.com.javaflix.Filme;
import br.com.javaflix.Serie;
import br.com.javaflix.ConteudoNaoEncontradoException;
import br.com.javaflix.client.PocketBaseClient;
import br.com.javaflix.client.dto.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ConteudoService
 */
@QuarkusTest
class ConteudoServiceTest {
    
    @Inject
    ConteudoService conteudoService;
    
    @InjectMock
    @RestClient
    PocketBaseClient pocketBaseClient;
    
    private ConteudoRecord filmeRecord;
    private ConteudoRecord serieRecord;
    private ConteudoListResponse listResponse;
    
    @BeforeEach
    void setup() {
        // Setup filme record
        filmeRecord = new ConteudoRecord();
        filmeRecord.id = "filme123";
        filmeRecord.titulo = "Matrix";
        filmeRecord.genero = "Ficção Científica";
        filmeRecord.classificacaoEtaria = 14;
        filmeRecord.tipo = "filme";
        filmeRecord.duracaoMinutos = 136;
        filmeRecord.diretor = "Wachowski";
        filmeRecord.avaliacoes = Arrays.asList(5, 5, 4, 5);
        
        // Setup série record
        serieRecord = new ConteudoRecord();
        serieRecord.id = "serie123";
        serieRecord.titulo = "Breaking Bad";
        serieRecord.genero = "Drama";
        serieRecord.classificacaoEtaria = 16;
        serieRecord.tipo = "serie";
        serieRecord.temporadas = 5;
        serieRecord.episodiosPorTemporada = 13;
        serieRecord.duracaoMediaEpisodio = 47;
        serieRecord.avaliacoes = Arrays.asList(5, 5, 5);
        
        // Setup list response
        listResponse = new ConteudoListResponse();
        listResponse.page = 1;
        listResponse.perPage = 100;
        listResponse.totalItems = 2;
        listResponse.totalPages = 1;
        listResponse.items = Arrays.asList(filmeRecord, serieRecord);
    }
    
    @Test
    @DisplayName("Deve listar todos os conteúdos")
    void testListarTodos() {
        // Given
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), any(), any()))
            .thenReturn(listResponse);
        
        // When
        List<Conteudo> conteudos = conteudoService.listarTodos();
        
        // Then
        assertNotNull(conteudos);
        assertEquals(2, conteudos.size());
        
        Conteudo primeiro = conteudos.get(0);
        assertTrue(primeiro instanceof Filme);
        assertEquals("Matrix", primeiro.getTitulo());
        
        Conteudo segundo = conteudos.get(1);
        assertTrue(segundo instanceof Serie);
        assertEquals("Breaking Bad", segundo.getTitulo());
        
        verify(pocketBaseClient, times(1))
            .getConteudos(1, 500, null, "-created");
    }
    
    @Test
    @DisplayName("Deve buscar conteúdo por título")
    void testBuscarPorTitulo() {
        // Given
        ConteudoListResponse singleResponse = new ConteudoListResponse();
        singleResponse.items = Arrays.asList(filmeRecord);
        
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), anyString(), any()))
            .thenReturn(singleResponse);
        
        // When
        Conteudo conteudo = conteudoService.buscarPorTitulo("Matrix");
        
        // Then
        assertNotNull(conteudo);
        assertEquals("Matrix", conteudo.getTitulo());
        assertTrue(conteudo instanceof Filme);
        
        verify(pocketBaseClient, times(1))
            .getConteudos(eq(1), eq(1), contains("Matrix"), eq(null));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando conteúdo não for encontrado")
    void testBuscarPorTituloNaoEncontrado() {
        // Given
        ConteudoListResponse emptyResponse = new ConteudoListResponse();
        emptyResponse.items = Arrays.asList();
        
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), anyString(), any()))
            .thenReturn(emptyResponse);
        
        // When & Then
        assertThrows(ConteudoNaoEncontradoException.class, () -> {
            conteudoService.buscarPorTitulo("Inexistente");
        });
    }
    
    @Test
    @DisplayName("Deve filtrar conteúdos por gênero")
    void testFiltrarPorGenero() {
        // Given
        ConteudoListResponse dramaResponse = new ConteudoListResponse();
        dramaResponse.items = Arrays.asList(serieRecord);
        
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), anyString(), any()))
            .thenReturn(dramaResponse);
        
        // When
        List<Conteudo> conteudos = conteudoService.filtrarPorGenero("Drama");
        
        // Then
        assertNotNull(conteudos);
        assertEquals(1, conteudos.size());
        assertEquals("Breaking Bad", conteudos.get(0).getTitulo());
        
        verify(pocketBaseClient, times(1))
            .getConteudos(eq(1), eq(500), contains("Drama"), eq("-created"));
    }
    
    @Test
    @DisplayName("Deve criar novo conteúdo")
    void testCriar() {
        // Given
        Filme novoFilme = new Filme("Inception", "Ficção", 14, 148, "Nolan");
        
        when(pocketBaseClient.createConteudo(any(ConteudoRequest.class)))
            .thenReturn(filmeRecord);
        
        // When
        Conteudo criado = conteudoService.criar(novoFilme);
        
        // Then
        assertNotNull(criado);
        assertEquals("Matrix", criado.getTitulo()); // Retorna o mock
        
        verify(pocketBaseClient, times(1))
            .createConteudo(any(ConteudoRequest.class));
    }
    
    @Test
    @DisplayName("Deve atualizar conteúdo existente")
    void testAtualizar() {
        // Given
        Filme filmeAtualizado = new Filme("Matrix Reloaded", "Ficção", 14, 138, "Wachowski");
        
        when(pocketBaseClient.updateConteudo(anyString(), any(ConteudoRequest.class)))
            .thenReturn(filmeRecord);
        
        // When
        Conteudo atualizado = conteudoService.atualizar("filme123", filmeAtualizado);
        
        // Then
        assertNotNull(atualizado);
        
        verify(pocketBaseClient, times(1))
            .updateConteudo(eq("filme123"), any(ConteudoRequest.class));
    }
    
    @Test
    @DisplayName("Deve remover conteúdo")
    void testRemover() {
        // Given
        doNothing().when(pocketBaseClient).deleteConteudo(anyString());
        
        // When
        conteudoService.remover("filme123");
        
        // Then
        verify(pocketBaseClient, times(1))
            .deleteConteudo("filme123");
    }
    
    @Test
    @DisplayName("Deve buscar múltiplos conteúdos em paralelo")
    void testBuscarParalelo() throws Exception {
        // Given
        ConteudoListResponse response1 = new ConteudoListResponse();
        response1.items = Arrays.asList(filmeRecord);
        
        ConteudoListResponse response2 = new ConteudoListResponse();
        response2.items = Arrays.asList(serieRecord);
        
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), anyString(), any()))
            .thenReturn(response1, response2);
        
        List<String> titulos = Arrays.asList("Matrix", "Breaking Bad");
        
        // When
        CompletableFuture<List<Conteudo>> future = conteudoService.buscarParalelo(titulos);
        List<Conteudo> conteudos = future.get();
        
        // Then
        assertNotNull(conteudos);
        assertEquals(2, conteudos.size());
        
        verify(pocketBaseClient, atLeast(2))
            .getConteudos(anyInt(), anyInt(), anyString(), any());
    }
    
    @Test
    @DisplayName("Deve listar conteúdos de forma assíncrona")
    void testListarTodosAsync() throws Exception {
        // Given
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), any(), any()))
            .thenReturn(listResponse);
        
        // When
        CompletableFuture<List<Conteudo>> future = conteudoService.listarTodosAsync();
        List<Conteudo> conteudos = future.get();
        
        // Then
        assertNotNull(conteudos);
        assertEquals(2, conteudos.size());
    }
    
    @Test
    @DisplayName("Deve validar título vazio na busca")
    void testBuscarPorTituloVazio() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            conteudoService.buscarPorTitulo("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            conteudoService.buscarPorTitulo(null);
        });
    }
    
    @Test
    @DisplayName("Deve mapear corretamente Filme do PocketBase")
    void testMapearFilme() {
        // Given
        ConteudoListResponse response = new ConteudoListResponse();
        response.items = Arrays.asList(filmeRecord);
        
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), any(), any()))
            .thenReturn(response);
        
        // When
        List<Conteudo> conteudos = conteudoService.listarTodos();
        Conteudo conteudo = conteudos.get(0);
        
        // Then
        assertTrue(conteudo instanceof Filme);
        Filme filme = (Filme) conteudo;
        assertEquals("Matrix", filme.getTitulo());
        assertEquals("Ficção Científica", filme.getGenero());
        assertEquals(14, filme.getClassificacaoEtaria());
        assertEquals(136, filme.getDuracaoMinutos());
        assertEquals("Wachowski", filme.getDiretor());
        assertEquals(4.75, filme.obterMediaAvaliacoes(), 0.01);
    }
    
    @Test
    @DisplayName("Deve mapear corretamente Série do PocketBase")
    void testMapearSerie() {
        // Given
        ConteudoListResponse response = new ConteudoListResponse();
        response.items = Arrays.asList(serieRecord);
        
        when(pocketBaseClient.getConteudos(anyInt(), anyInt(), any(), any()))
            .thenReturn(response);
        
        // When
        List<Conteudo> conteudos = conteudoService.listarTodos();
        Conteudo conteudo = conteudos.get(0);
        
        // Then
        assertTrue(conteudo instanceof Serie);
        Serie serie = (Serie) conteudo;
        assertEquals("Breaking Bad", serie.getTitulo());
        assertEquals("Drama", serie.getGenero());
        assertEquals(16, serie.getClassificacaoEtaria());
        assertEquals(5, serie.getTemporadas());
        assertEquals(13, serie.getEpisodiosPorTemporada());
        assertEquals(47, serie.getDuracaoMediaEpisodioMinutos());
        assertEquals(5.0, serie.obterMediaAvaliacoes(), 0.01);
    }
}

// Made with Bob
