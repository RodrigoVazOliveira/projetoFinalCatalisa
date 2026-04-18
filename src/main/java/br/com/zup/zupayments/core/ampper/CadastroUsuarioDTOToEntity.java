package br.com.zup.zupayments.core.ampper;

import br.com.zup.zupayments.dtos.usuario.entrada.CadastrarUsuarioDTO;
import br.com.zup.zupayments.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CadastroUsuarioDTOToEntity implements Mapper<CadastrarUsuarioDTO
        , Usuario> {
    private static final Logger log = LoggerFactory.getLogger(CadastroUsuarioDTOToEntity.class);

    @Override
    public Usuario map(CadastrarUsuarioDTO cadastrarUsuarioDTO) {
        log.debug("cadastrarUsuarioDTO: {}", cadastrarUsuarioDTO);
        log.info("Converter CadastroDTO para Usuario - cadastrarUsuarioDTO: {}", cadastrarUsuarioDTO);
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(cadastrarUsuarioDTO.nomeCompleto());
        usuario.setEmail(cadastrarUsuarioDTO.email());
        usuario.setSenha(cadastrarUsuarioDTO.senha());
        usuario.setNivelDeAcesso(cadastrarUsuarioDTO.nivelDeAcesso());
        usuario.setAtivo(true);
        log.debug("usuario: {}", usuario);

        return usuario;
    }
}
