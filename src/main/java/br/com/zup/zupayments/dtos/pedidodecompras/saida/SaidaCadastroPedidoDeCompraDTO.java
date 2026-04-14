package br.com.zup.zupayments.dtos.pedidodecompras.saida;

import br.com.zup.zupayments.enums.FormaDePagamento;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record SaidaCadastroPedidoDeCompraDTO(
        Long numeroDePedido,

        @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING, timezone = "America/Sao_Paulo")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        LocalDate dataDeVencimento,

        Double saldo,

        @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING, timezone = "America/Sao_Paulo")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        LocalDate dataDePagamento,

        Responsavel responsavel,

        @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING, timezone = "America/Sao_Paulo")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        LocalDate dataLimiteEnvio,

        FormaDePagamento formaDePagamento,

        PedidoDeCompraFornecedorDTO fornecedor
) {
    public static SaidaCadastroPedidoDeCompraDTO converterModeloParaDto(PedidoDeCompra pedidoDeCompra) {
        return new SaidaCadastroPedidoDeCompraDTO(
                pedidoDeCompra.getNumeroDePedido(),
                pedidoDeCompra.getDataDeVencimento(),
                pedidoDeCompra.getSaldo(),
                pedidoDeCompra.getDataDePagamento(),
                pedidoDeCompra.getResponsavel(),
                pedidoDeCompra.getDataLimiteEnvio(),
                pedidoDeCompra.getFormaDePagamento(),
                PedidoDeCompraFornecedorDTO.converterModeloParaDto(pedidoDeCompra.getFornecedor())
        );
    }

    public static Iterable<SaidaCadastroPedidoDeCompraDTO> converterListaDeModeloParaListaDto(
            Iterable<PedidoDeCompra> pedidoDeCompras) {
        List<SaidaCadastroPedidoDeCompraDTO> dtos = new ArrayList<>();

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            dtos.add(converterModeloParaDto(pedidoDeCompra));
        }

        return dtos;
    }
}
