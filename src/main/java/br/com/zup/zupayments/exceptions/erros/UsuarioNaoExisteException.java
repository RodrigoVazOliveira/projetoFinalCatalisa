package br.com.zup.zupayments.exceptions.erros;

public class UsuarioNaoExisteException extends RuntimeException{
    private static final Integer STATUS = 400;
    private static final String USUARIO_NAO_ENCONTRADO = "usuário não cadastrado";
    private static final String MOTIVO = "BAD REQUEST";

    public UsuarioNaoExisteException(String message){super(message);}

    public Integer getStatus() {
        return STATUS;
    }

    public String getTipoDeErro() {
        return USUARIO_NAO_ENCONTRADO;
    }

    public String getMotivo() {
        return MOTIVO;
    }
}