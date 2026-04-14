package br.com.zup.zupayments.dtos.pedidodecompras.entrada;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record FiltroPedidoDeCompraComNotaFiscalPendenteDTO(
        Double valorMinimo,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataInicial,

        Boolean ativo
) {
}
