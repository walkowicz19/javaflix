package br.com.javaflix.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Request para criar/atualizar conteúdo
 */
public class ConteudoRequest {
    public String titulo;
    public String genero;
    
    @JsonProperty("classificacao_etaria")
    public int classificacaoEtaria;
    
    public String tipo;
    public List<Integer> avaliacoes;
    
    @JsonProperty("media_avaliacoes")
    public Double mediaAvaliacoes;
    
    @JsonProperty("duracao_minutos")
    public Integer duracaoMinutos;
    
    public String diretor;
    public Integer temporadas;
    
    @JsonProperty("episodios_por_temporada")
    public Integer episodiosPorTemporada;
    
    @JsonProperty("duracao_media_episodio")
    public Integer duracaoMediaEpisodio;
}

// Made with Bob
