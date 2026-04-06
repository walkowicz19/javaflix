package br.com.javaflix.client.dto;

import java.util.List;

/**
 * Resposta paginada de conteúdos do PocketBase
 */
public class ConteudoListResponse {
    public int page;
    public int perPage;
    public int totalItems;
    public int totalPages;
    public List<ConteudoRecord> items;
}

// Made with Bob
