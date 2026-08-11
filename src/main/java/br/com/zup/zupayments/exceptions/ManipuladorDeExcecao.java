package br.com.zup.zupayments.exceptions;

import br.com.zup.zupayments.exceptions.erros.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ManipuladorDeExcecao extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ObjetoDeErro> objetosDeErro = criarObjetoDeErro(ex);
        RespostaDeErro respostaDeErro = new RespostaDeErro("Validação", status.value(),
                status.toString(), objetosDeErro);

        return ResponseEntity.status(status).body(respostaDeErro);
    }

    private List<ObjetoDeErro> criarObjetoDeErro(MethodArgumentNotValidException exception) {
        List<ObjetoDeErro> objetosDeErro = new ArrayList<>();
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();

        for (FieldError listaDeErros : errors) {
            ObjetoDeErro objetoDeErro = new ObjetoDeErro(listaDeErros.getDefaultMessage(),
                    listaDeErros.getField());
            objetosDeErro.add(objetoDeErro);
        }

        return objetosDeErro;
    }

    @ExceptionHandler({ExcecaoBasica.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErro manipularRuntimeException(ExcecaoBasica erro) {
        ObjetoDeErro objetoDeErro = new ObjetoDeErro(erro.getMessage(), erro.getCampo());
        RespostaDeErro respostaDeErro = new RespostaDeErro(erro.getMessage(), erro.getStatus(),
                erro.getMotivo(), List.of(objetoDeErro));

        return respostaDeErro;
    }

    @ExceptionHandler({UsuarioJaExisteComEmailException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo usuarioJaExisteComEmailException(UsuarioJaExisteComEmailException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({UsuarioNaoExisteException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo usuarioNaoExisteException(UsuarioNaoExisteException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({ResponsavelNaoExisteException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo responsavelNaoExisteException(ResponsavelNaoExisteException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({ResponsavelJaCadastradoException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo responsavelJaCadastradoException(ResponsavelJaCadastradoException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({PedidoDeCompraNaoExisteException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo pedidoDeCompraNaoExisteException(PedidoDeCompraNaoExisteException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({NotaFiscalNaoCadastradaException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo notaFiscalNaoCadastradaException(NotaFiscalNaoCadastradaException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({NotaFiscalCadastradaException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo notaFiscalCadastradaException(NotaFiscalCadastradaException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({FornecedorNaoCadastrado.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo fornecedorNaoCadastrado(FornecedorNaoCadastrado ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({FornecedorCadastradoException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo fornecedorCadastradoException(FornecedorCadastradoException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }

    @ExceptionHandler({PedidoDeCompraSemSaldoException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespostaDeErroSemCampo pedidoDeCompraSemSaldoException(PedidoDeCompraSemSaldoException ex) {
        return new RespostaDeErroSemCampo(ex.getTipoDeErro(), ex.getStatus(), ex.getMotivo(), ex.getMessage());
    }
}
