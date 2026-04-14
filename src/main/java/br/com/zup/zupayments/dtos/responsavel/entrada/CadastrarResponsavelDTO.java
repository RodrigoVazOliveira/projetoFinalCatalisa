package br.com.zup.zupayments.dtos.responsavel.entrada;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CadastrarResponsavelDTO(
    @NotBlank(message = "{validacao.campo_em_branco}")
    @NotEmpty(message = "{validacao.campo_vazio}")
    @Size(max = 255, message = "{validacao.tamanho_campo}")
    @Email(message = "{validacao.email_invalido}")
    String email,

    @NotBlank(message = "{validacao.campo_em_branco}")
    @NotEmpty(message = "{validacao.campo_vazio}")
    @Size(max = 100, message = "{validacao.tamanho_campo}")
    String nomeCompleto,

    @NotBlank(message = "{validacao.campo_em_branco}")
    @NotEmpty(message = "{validacao.campo_vazio}")
    @Size(max = 70, message = "{validacao.tamanho_campo}")
    String nomeDoProjeto
) {
}
