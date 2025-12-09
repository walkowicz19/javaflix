import java.util.ArrayList;
import java.util.List;

public class PlataformaStreaming {
    private String nome;
    private List<Conteudo> catalogo;

    public PlataformaStreaming(String nome) {
        this.nome = nome;
        this.catalogo = new ArrayList<>();
    }

    public void adicionarConteudo(Conteudo conteudo) {
        catalogo.add(conteudo);
    }

    public void listarCatalogo() {
        System.out.println("\n=== Catálogo " + nome + " ===");
        for (Conteudo c : catalogo) {
            System.out.println(c);
        }
    }

    public Conteudo buscar(String titulo) throws ConteudoNaoEncontradoException {
        for (Conteudo c : catalogo) {
            if (c.getTitulo().equalsIgnoreCase(titulo)) {
                return c;
            }
        }
        throw new ConteudoNaoEncontradoException("Conteúdo '" + titulo + "' não encontrado.");
    }

    public void assistirConteudo(Usuario usuario, Conteudo conteudo) throws ClassificacaoIndicativaException {
        if (usuario.getIdade() < conteudo.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException(
                "Usuário " + usuario.getNome() + " não tem idade para assistir '" + conteudo.getTitulo() + "'"
            );
        }
        System.out.println(usuario.getNome() + " está assistindo: " + conteudo.getTitulo());
        System.out.println("Plano: " + usuario.getTipoAssinatura());
    }

    public List<Conteudo> filtrar(String genero) {
        List<Conteudo> resultado = new ArrayList<>();
        for (Conteudo c : catalogo) {
            if (c.getGenero().equalsIgnoreCase(genero)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<Conteudo> filtrar(int idadeMaxima) {
        List<Conteudo> resultado = new ArrayList<>();
        for (Conteudo c : catalogo) {
            if (c.getClassificacaoEtaria() <= idadeMaxima) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<Conteudo> getCatalogo() {
        return catalogo;
    }
}