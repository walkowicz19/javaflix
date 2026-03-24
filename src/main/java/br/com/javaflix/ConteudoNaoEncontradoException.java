package br.com.javaflix;
public class ConteudoNaoEncontradoException extends Exception {
    public ConteudoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
