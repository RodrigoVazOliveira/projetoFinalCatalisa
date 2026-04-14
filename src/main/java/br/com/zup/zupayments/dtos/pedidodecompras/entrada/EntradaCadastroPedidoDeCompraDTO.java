package br.com.zup.zupayments.dtos.pedidodecompras.entrada;

import br.com.zup.zupayments.enums.FormaDePagamento;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EntradaCadastroPedidoDeCompraDTO(
        @NotNull(message = "{validacao.campo_obrigatorio}")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataDeVencimento,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        Double saldo,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataDePagamento,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Email(message = "{validacao.email_invalido}")
        String emailResponsavel,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataLimiteEnvio,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        FormaDePagamento formaDePagamento,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        String cnpjOuCpf
) {
    public PedidoDeCompra converterDtoParaModelo() {
        PedidoDeCompra pedidoDeCompra = new PedidoDeCompra();

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCnpjOuCpf(cnpjOuCpf);

        Responsavel responsavel = new Responsavel();
        responsavel.setEmail(this.emailResponsavel);

        pedidoDeCompra.setDataDeVencimento(this.dataDeVencimento);
        pedidoDeCompra.setSaldo(this.saldo);
        pedidoDeCompra.setDataDePagamento(this.dataDePagamento);
        pedidoDeCompra.setResponsavel(responsavel);
        pedidoDeCompra.setDataLimiteEnvio(this.dataLimiteEnvio);
        pedidoDeCompra.setFormaDePagamento(this.formaDePagamento);
        pedidoDeCompra.setFornecedor(fornecedor);
        pedidoDeCompra.setCancelado(false);
        return pedidoDeCompra;
    }
}
