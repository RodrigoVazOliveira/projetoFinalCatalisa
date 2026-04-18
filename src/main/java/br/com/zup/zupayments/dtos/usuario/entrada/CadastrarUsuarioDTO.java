package br.com.zup.zupayments.dtos.usuario.entrada;

import br.com.zup.zupayments.enums.RolesEnum;
import jakarta.validation.constraints.*;

public record CadastrarUsuarioDTO(
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(min = 5, max = 50, message = "{validacao.tamanho_campo}")
        String nomeCompleto,

        @NotBlank(message = "{validcao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(min = 5, max = 150, message = "{validacao.tamanho_campo}")
        @Email(message = "{validacao.email_invalido}")
        String email,

        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(min = 6, max = 16, message = "{validacao.tamanho_campo}")
        String senha,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        RolesEnum nivelDeAcesso
) {

}
