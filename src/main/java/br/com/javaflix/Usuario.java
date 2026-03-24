package br.com.javaflix;
public class Usuario {
    private String nome;
    private int idade;
    private boolean assinaturaPremium;

    // Sobrecarga de Construtores (R4)

    // Construtor 1: Cria usuário com plano Grátis (padrão)
    public Usuario(String nome, int idade) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (idade < 0 || idade > 150) {
            throw new IllegalArgumentException("Idade inválida");
        }
        this.nome = nome.trim();
        this.idade = idade;
        this.assinaturaPremium = false;
    }

    // Construtor 2: Cria usuário definindo se é Premium
    public Usuario(String nome, int idade, boolean isPremium) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (idade < 0 || idade > 150) {
            throw new IllegalArgumentException("Idade inválida");
        }
        this.nome = nome.trim();
        this.idade = idade;
        this.assinaturaPremium = isPremium;
    }

    public String getNome() { return nome; }
    public int getIdade() { return idade; }
    public boolean isPremium() { return assinaturaPremium; }

    public String getTipoAssinatura() {
        return assinaturaPremium ? "Premium (4K)" : "Básico (Com Anúncios)";
    }
}
