package br.com.javaflix;
public class ConteudoNaoEncontradoException extends RuntimeException {
    public ConteudoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
