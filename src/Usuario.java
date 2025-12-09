public class Usuario {
    private String nome;
    private int idade;
    private boolean assinaturaPremium;

    // Sobrecarga de Construtores (R4)

    // Construtor 1: Cria usuário com plano Grátis (padrão)
    public Usuario(String nome, int idade) {
        this.nome = nome;
        this.idade = idade;
        this.assinaturaPremium = false;
    }

    // Construtor 2: Cria usuário definindo se é Premium
    public Usuario(String nome, int idade, boolean isPremium) {
        this.nome = nome;
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