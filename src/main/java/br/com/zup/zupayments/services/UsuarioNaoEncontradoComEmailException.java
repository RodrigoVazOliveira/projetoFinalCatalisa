package br.com.zup.zupayments.services;

public class UsuarioNaoEncontradoComEmailException extends RuntimeException {
    public UsuarioNaoEncontradoComEmailException(String message) {
        super(message);
    }
}
