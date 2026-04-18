package br.com.zup.zupayments.dtos.notafiscal.entrada;

import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record CadastrarNotaFiscalDTO(
        @NotNull(message = "{validacao.campo_obrigatorio}")
        Long numeroDaNota,

        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        String cnpjOuCpfFornecedor,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        Double valorAPagar,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataDeEmissao,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        List<UUID> pedidoDeCompras,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataDeEnvio,

        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Email(message = "{validacao.email_invalido}")
        String emailDoResponsavel
) {
    public NotaFiscal converterDtoParaModelo() {
        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setNumeroDaNota(this.numeroDaNota);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCnpjOuCpf(this.cnpjOuCpfFornecedor);

        notaFiscal.setFornecedor(fornecedor);
        notaFiscal.setValorAPagar(this.valorAPagar);
        notaFiscal.setDataDeEmissao(this.dataDeEmissao);
        notaFiscal.setPedidoDeCompra(gerarListaDePedidoDeCompraParaModelo());
        notaFiscal.setDataDeEnvio(this.dataDeEnvio);

        Responsavel responsavel = new Responsavel();
        responsavel.setEmail(this.emailDoResponsavel);
        notaFiscal.setResponsavel(responsavel);
        notaFiscal.setCancelar(false);

        return notaFiscal;
    }

    private List<PedidoDeCompra> gerarListaDePedidoDeCompraParaModelo() {
        List<PedidoDeCompra> pedidos = new ArrayList<>();

        for (UUID numeroPedido : this.pedidoDeCompras) {
            PedidoDeCompra pedido = new PedidoDeCompra();
            pedido.setNumeroDePedido(numeroPedido);
            pedidos.add(pedido);
        }

        return pedidos;
    }
}
