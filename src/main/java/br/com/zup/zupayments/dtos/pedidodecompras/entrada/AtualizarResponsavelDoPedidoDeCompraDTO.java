package br.com.zup.zupayments.dtos.pedidodecompras.entrada;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AtualizarResponsavelDoPedidoDeCompraDTO(
        @NotNull(message = "{validacao.campo_obrigatorio}")
        UUID numeroPedidoDeCompra,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Email(message = "{validacao.email_invalido}")
        String emailResponsavel
) {
}
