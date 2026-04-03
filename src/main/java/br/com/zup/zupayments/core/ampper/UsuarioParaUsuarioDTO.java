package br.com.zup.zupayments.core.ampper;

import br.com.zup.zupayments.dtos.usuario.saida.UsuarioDTO;
import br.com.zup.zupayments.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UsuarioParaUsuarioDTO implements Mapper<Usuario, UsuarioDTO> {
    private static final Logger log = LoggerFactory.getLogger(UsuarioParaUsuarioDTO.class);

    @Override
    public UsuarioDTO map(Usuario usuario) {
        log.info("Converter Usuario para UsuarioDTO");
        log.debug("usuario: {}", usuario);

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNomeCompleto(),
                usuario.getNivelDeAcesso(),
                usuario.getAtivo()
        );
    }
}
