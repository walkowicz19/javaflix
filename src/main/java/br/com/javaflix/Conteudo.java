package br.com.javaflix;

import java.util.ArrayList;
import java.util.List;

public abstract class Conteudo implements Avaliavel {
    // Atributos (R2)
    private String titulo;
    private String genero;
    private int classificacaoEtaria; // Ex: 10, 12, 16, 18 anos
    private List<Double> avaliacoes = new ArrayList<>();

    public Conteudo(String titulo, String genero, int classificacaoEtaria) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("Gênero não pode ser vazio");
        }
        if (classificacaoEtaria < 0 || classificacaoEtaria > 18) {
            throw new IllegalArgumentException("Classificação etária inválida");
        }
        this.titulo = titulo.trim();
        this.genero = genero.trim();
        this.classificacaoEtaria = classificacaoEtaria;
    }

    // Método Abstrato (R6 - Polimorfismo):
    // Cada tipo de conteúdo calcula sua duração total de jeito diferente.
    public abstract int getDuracaoTotalEmMinutos();

    // Método comum a todos
    public String getDetalhesBasicos() {
        return String.format("Título: %s | Gênero: %s | Classificação: +%d anos",
                titulo, genero, classificacaoEtaria);
    }

    // Getters para a serialização JSON nativa (comunicação com Front-end React)
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getClassificacaoEtaria() { return classificacaoEtaria; }
    
    // Mapeamento necessário para substituir o antigo conteudoToJson do HttpServer
    public int getClassificacao() { return classificacaoEtaria; }
    
    public String getTipo() { 
        return this.getClass().getSimpleName().equals("Filme") ? "Filme" : "Serie"; 
    }

    @Override
    public void avaliar(double nota) {
        this.avaliacoes.add(nota);
    }

    @Override
    public double getMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) {
            return 0.0;
        }
        double soma = 0;
        for (double d : avaliacoes) {
            soma += d;
        }
        return soma / avaliacoes.size();
    }

    public double obterMediaAvaliacoes() {
        return getMediaAvaliacoes();
    }
}
