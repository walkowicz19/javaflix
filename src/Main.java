
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static PlataformaStreaming netflixClone;
    private static Usuario usuarioLogado;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarSistema();

        int opcao = 0;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            try { // Bloco Try-Catch Principal (R7)
                switch (opcao) {
                    case 1:
                        listarTudo();
                        break;
                    case 2:
                        buscarEAssistir();
                        break;
                    case 3:
                        filtrarPorGenero();
                        break;
                    case 4:
                        trocarUsuario();
                        break;
                    case 5:
                        System.out.println("Encerrando Stream... Até logo!");
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (ConteudoNaoEncontradoException e) {
                System.err.println("🔍 ERRO DE BUSCA: " + e.getMessage());
            } catch (ClassificacaoIndicativaException e) {
                System.err.println("🔞 BLOQUEIO PARENTAL: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("❌ ERRO INESPERADO: " + e.getMessage());
            }

            if (opcao != 5) {
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine();
            }

        } while (opcao != 5);

        scanner.close();
    }

    private static void inicializarSistema() {
        netflixClone = new PlataformaStreaming("JavaFlix");

        // Populando o catálogo com Filmes e Séries (Polimorfismo na prática)
        netflixClone.adicionarConteudo(new Filme("O Poderoso Chefão", "Drama", 16, 175, "Coppola"));
        netflixClone.adicionarConteudo(new Filme("Shrek", "Animação", 0, 90, "Andrew Adamson"));
        netflixClone.adicionarConteudo(new Filme("Matrix", "Ficção", 14, 136, "Wachowski"));

        netflixClone.adicionarConteudo(new Serie("Breaking Bad", "Drama", 18, 5, 13, 50));
        netflixClone.adicionarConteudo(new Serie("Stranger Things", "Ficção", 14, 4, 8, 60));

        // Cria usuário padrão
        usuarioLogado = new Usuario("Visitante", 18, true);
    }

    private static void exibirMenu() {
        System.out.println("\n=== " + "JavaFlix" + " ===");
        System.out.println("Usuário: " + usuarioLogado.getNome() + " | Idade: " + usuarioLogado.getIdade() + " | Plano: " + usuarioLogado.getTipoAssinatura());
        System.out.println("-------------------------");
        System.out.println("1. Ver Catálogo Completo");
        System.out.println("2. Buscar Título e Assistir");
        System.out.println("3. Filtrar por Gênero");
        System.out.println("4. Trocar Usuário (Simular Idade)");
        System.out.println("5. Sair");
        System.out.println("-------------------------");
    }

    private static void listarTudo() {
        netflixClone.listarCatalogo();
    }

    private static void buscarEAssistir() throws ConteudoNaoEncontradoException, ClassificacaoIndicativaException {
        System.out.print("Digite o nome do Filme/Série: ");
        String nome = scanner.nextLine();

        // Busca (pode lançar ConteudoNaoEncontradoException)
        Conteudo c = netflixClone.buscar(nome);

        System.out.println("Conteúdo encontrado: " + c.getDetalhesBasicos());

        // Tenta assistir (pode lançar ClassificacaoIndicativaException)
        netflixClone.assistirConteudo(usuarioLogado, c);
    }

    private static void filtrarPorGenero() throws ConteudoNaoEncontradoException {
        System.out.print("Digite o gênero (Drama, Ficção, Animação): ");
        String genero = scanner.nextLine();

        List<Conteudo> lista = netflixClone.filtrar(genero);

        System.out.println("\n--- Gênero: " + genero + " ---");
        for (Conteudo c : lista) {
            System.out.println("- " + c.getTitulo() + " (Classificação: " + c.getClassificacaoEtaria() + ")");
        }
    }

    private static void trocarUsuario() {
        System.out.println("--- Trocar Usuário ---");
        System.out.print("Novo Nome: ");
        String nome = scanner.nextLine();
        int idade = lerInteiro("Nova Idade: ");

        System.out.println("Escolha o plano:");
        System.out.println("1. Básico (Grátis)");
        System.out.println("2. Premium (Pago)");
        int op = lerInteiro("Opção: ");
        boolean isPremium = (op == 2);

        // Uso da Sobrecarga do Construtor
        if (op == 1) {
            usuarioLogado = new Usuario(nome, idade); // Chama construtor 1
        } else {
            usuarioLogado = new Usuario(nome, idade, true); // Chama construtor 2
        }
        System.out.println("Usuário alterado com sucesso!");
    }

    // Função auxiliar segura para ler inteiros
    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int valor = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Erro: Digite apenas números inteiros.");
                scanner.nextLine(); // Limpar buffer do erro
            }
        }
    }
}