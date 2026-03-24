package br.com.javaflix;
public class Serie extends Conteudo {

    private int temporadas;
    private int episodiosPorTemporada;
    private int minutosPorEpisodio;

    public Serie(String titulo, String genero, int classificacaoEtaria, int temporadas, int epPorTemp, int minPorEp) {
        super(titulo, genero, classificacaoEtaria);
        this.temporadas = temporadas;
        this.episodiosPorTemporada = epPorTemp;
        this.minutosPorEpisodio = minPorEp;
    }

    // Polimorfismo (R6): Série calcula duração multiplicando episódios
    @Override
    public int getDuracaoTotalEmMinutos() {
        return temporadas * episodiosPorTemporada * minutosPorEpisodio;
    }

    @Override
    public String toString() {
        return getDetalhesBasicos() + " | Tipo: SÉRIE | Temps: " + temporadas +
                " | Eps Total: " + (temporadas * episodiosPorTemporada);
    }
}
