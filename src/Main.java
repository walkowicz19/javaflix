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

            try {
                switch (opcao) {
                    case 1: listarTudo(); break;
                    case 2: buscarEAssistir(); break;
                    case 3: filtrarConteudo(); break; // Atualizado para usar Sobrecarga
                    case 4: trocarUsuario(); break;
                    case 5: avaliarTitulo(); break; // Nova opção da Interface
                    case 6: System.out.println("Encerrando..."); break;
                    default: System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("ERRO: " + e.getMessage());
            }

            if (opcao != 6) {
                System.out.println("\n[Enter] para continuar...");
                scanner.nextLine();
            }

        } while (opcao != 6);
        scanner.close();
    }

    private static void inicializarSistema() {
        netflixClone = new PlataformaStreaming("JavaFlix");
        // Filmes
        netflixClone.adicionarConteudo(new Filme("O Poderoso Chefão", "Drama", 16, 175, "Coppola"));
        netflixClone.adicionarConteudo(new Filme("Shrek", "Animação", 0, 90, "Andrew Adamson"));
        // Séries
        netflixClone.adicionarConteudo(new Serie("Breaking Bad", "Drama", 18, 5, 13, 50));
        netflixClone.adicionarConteudo(new Serie("Stranger Things", "Ficção", 14, 4, 8, 60));
        
        usuarioLogado = new Usuario("Visitante", 18, true);
    }

    private static void exibirMenu() {
        System.out.println("\n=== JavaFlix (" + usuarioLogado.getNome() + ") ===");
        System.out.println("1. Ver Catálogo");
        System.out.println("2. Assistir");
        System.out.println("3. Filtrar (Sobrecarga)");
        System.out.println("4. Trocar Usuário");
        System.out.println("5. Avaliar Título (Interface)");
        System.out.println("6. Sair");
    }

    private static void listarTudo() {
        netflixClone.listarCatalogo();
    }

    private static void buscarEAssistir() throws Exception {
        System.out.print("Nome do título: ");
        String nome = scanner.nextLine();
        Conteudo c = netflixClone.buscar(nome);
        netflixClone.assistirConteudo(usuarioLogado, c);
    }

    // Demonstração da Sobrecarga (Requisito 4)
    private static void filtrarConteudo() {
        System.out.println("Filtrar por: [1] Gênero | [2] Idade Máxima");
        int tipo = lerInteiro("Opção: ");

        List<Conteudo> resultado;
        
        if (tipo == 1) {
            System.out.print("Digite o gênero: ");
            String genero = scanner.nextLine();
            // Chama filtrar(String)
            resultado = netflixClone.filtrar(genero); 
        } else {
            int idade = lerInteiro("Idade máxima permitida: ");
            // Chama filtrar(int) - MESMO NOME, PARÂMETRO DIFERENTE
            resultado = netflixClone.filtrar(idade); 
        }

        System.out.println("\n--- Resultados do Filtro ---");
        if(resultado.isEmpty()) System.out.println("Nada encontrado.");
        for (Conteudo c : resultado) {
            System.out.println("- " + c.getTitulo() + " (" + c.getDetalhesBasicos() + ")");
        }
    }

    // Demonstração da Interface (Requisito 6/Extra)
    private static void avaliarTitulo() throws Exception {
        System.out.print("Qual título deseja avaliar? ");
        String nome = scanner.nextLine();
        Conteudo c = netflixClone.buscar(nome);
        
        System.out.print("Nota (0-10): ");
        double nota = scanner.nextDouble();
        scanner.nextLine(); // limpar buffer

        c.avaliar(nota); // Método da Interface Avaliavel
        System.out.println("Avaliação registrada! Nova média: " + c.getMediaAvaliacoes());
    }

    private static void trocarUsuario() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        int idade = lerInteiro("Idade: ");
        usuarioLogado = new Usuario(nome, idade);
        System.out.println("Usuário trocado!");
    }

    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int v = scanner.nextInt();
                scanner.nextLine();
                return v;
            } catch (InputMismatchException e) {
                System.out.println("Digite um número válido.");
                scanner.nextLine();
            }
        }
    }
}