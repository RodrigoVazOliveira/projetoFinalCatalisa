package br.com.zup.zupayments.exceptions.erros;

public class NotaFiscalNaoCadastradaException extends RuntimeException{

    private Integer status = 400;
    private String tipoDeErro = "Nota fiscal não esta cadastrada";
    private String motivo = "BAD REQUEST";

    public NotaFiscalNaoCadastradaException(String message) {
        super(message);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTipoDeErro() {
        return tipoDeErro;
    }

    public void setTipoDeErro(String tipoDeErro) {
        this.tipoDeErro = tipoDeErro;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
