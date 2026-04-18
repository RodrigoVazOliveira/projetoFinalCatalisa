package br.com.zup.zupayments.dtos.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank(message = "{validacao.campo_em_branco}")
        @Size(max = 150, message = "{validacao.tamanho_campo}")
        @Email(message = "{validacao.email_invalido}")
        String email,

        @NotBlank(message = "{validacao.campo_em_branco}")
        @Size(min = 6, max = 16, message = "{validacao.tamanho_campo}")
        String senha
) {
}