package br.com.zup.zupayments.services;

public class ErroEnviarEmailException extends RuntimeException {
    public ErroEnviarEmailException(String message) {
        super(message);
    }
}
