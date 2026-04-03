package br.com.zup.zupayments.dtos.usuario.entrada;

import br.com.zup.zupayments.enums.RolesEnum;

public record NivelDeAcessoUsuarioDTO(
        private RolesEnum nivelDeAcesso
) {
}
