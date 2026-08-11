package br.com.zup.zupayments.jwt;

import br.com.zup.zupayments.models.Usuario;
import br.com.zup.zupayments.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioLoginService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioLoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        Usuario usuario =
                usuarioOptional
                        .orElseThrow(
                                () -> new UsernameNotFoundException("Email não cadastrado"));

        return new UsuarioLogin(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNomeCompleto(),
                usuario.getNivelDeAcesso()
        );
    }
}