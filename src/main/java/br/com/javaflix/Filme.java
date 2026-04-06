package br.com.javaflix;
public class Filme extends Conteudo {

    private int duracaoMinutos;
    private String diretor;

    public Filme(String titulo, String genero, int classificacaoEtaria, int duracaoMinutos, String diretor) {
        super(titulo, genero, classificacaoEtaria);
        this.duracaoMinutos = duracaoMinutos;
        this.diretor = diretor;
    }

    // Getters para serialização JSON
    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public String getDiretor() {
        return diretor;
    }

    // Polimorfismo (R6): Implementação específica para Filme
    @Override
    public int getDuracaoTotalEmMinutos() {
        return this.duracaoMinutos;
    }

    @Override
    public String toString() {
        return getDetalhesBasicos() + " | Tipo: FILME | Duração: " + duracaoMinutos + "min | Diretor: " + diretor;
    }
}
