package br.com.zup.zupayments.dtos.usuario.saida;

import br.com.zup.zupayments.enums.RolesEnum;

import java.util.UUID;

public record UsuarioDTO(
        UUID id,
        String email,
        String nomeCompleto,
        RolesEnum nivelDeAcesso,
        Boolean ativo
) {

}
