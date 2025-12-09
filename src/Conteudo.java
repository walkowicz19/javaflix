public abstract class Conteudo implements Avaliavel {
    // Atributos (R2)
    private String titulo;
    private String genero;
    private int classificacaoEtaria; 
    
    // Novos atributos para a Interface
    private double somaAvaliacoes;
    private int totalAvaliacoes;

    public Conteudo(String titulo, String genero, int classificacaoEtaria) {
        this.titulo = titulo;
        this.genero = genero;
        this.classificacaoEtaria = classificacaoEtaria;
        this.somaAvaliacoes = 0;
        this.totalAvaliacoes = 0;
    }

    // Método Abstrato (R6 - Polimorfismo)
    public abstract int getDuracaoTotalEmMinutos();

    // Implementação da Interface Avaliavel
    @Override
    public void avaliar(double nota) {
        this.somaAvaliacoes += nota;
        this.totalAvaliacoes++;
    }

    @Override
    public double getMediaAvaliacoes() {
        if (totalAvaliacoes == 0) return 0;
        return somaAvaliacoes / totalAvaliacoes;
    }

    public String getDetalhesBasicos() {
        return String.format("Título: %s | Gênero: %s | Classificação: +%d anos | ⭐ %.1f",
                titulo, genero, classificacaoEtaria, getMediaAvaliacoes());
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getClassificacaoEtaria() { return classificacaoEtaria; }
}