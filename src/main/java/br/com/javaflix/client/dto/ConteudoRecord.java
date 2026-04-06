package br.com.javaflix.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Representa um registro de conteúdo no PocketBase
 */
public class ConteudoRecord {
    
    public String id;
    
    @JsonProperty("collectionId")
    public String collectionId;
    
    @JsonProperty("collectionName")
    public String collectionName;
    
    public String created;
    public String updated;
    
    // Campos comuns
    public String titulo;
    public String genero;
    
    @JsonProperty("classificacao_etaria")
    public int classificacaoEtaria;
    
    public String tipo; // "filme" ou "serie"
    
    public List<Integer> avaliacoes;
    
    @JsonProperty("media_avaliacoes")
    public Double mediaAvaliacoes;
    
    // Campos específicos de Filme
    @JsonProperty("duracao_minutos")
    public Integer duracaoMinutos;
    
    public String diretor;
    
    // Campos específicos de Série
    public Integer temporadas;
    
    @JsonProperty("episodios_por_temporada")
    public Integer episodiosPorTemporada;
    
    @JsonProperty("duracao_media_episodio")
    public Integer duracaoMediaEpisodio;
}

// Made with Bob
