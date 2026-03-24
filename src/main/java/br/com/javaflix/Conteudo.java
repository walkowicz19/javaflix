package br.com.javaflix;
public abstract class Conteudo {
    // Atributos (R2)
    private String titulo;
    private String genero;
    private int classificacaoEtaria; // Ex: 10, 12, 16, 18 anos

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

    // Getters
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getClassificacaoEtaria() { return classificacaoEtaria; }
}
