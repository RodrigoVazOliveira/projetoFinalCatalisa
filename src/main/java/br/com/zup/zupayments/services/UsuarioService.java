package br.com.zup.zupayments.services;

import br.com.zup.zupayments.enums.RolesEnum;
import br.com.zup.zupayments.exceptions.erros.UsuarioJaExisteComEmailException;
import br.com.zup.zupayments.exceptions.erros.UsuarioNaoExisteException;
import br.com.zup.zupayments.models.Usuario;
import br.com.zup.zupayments.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void cadastrarNovoUsuario(Usuario usuario) {
        usuarioExiste(usuario);
        usuario.setSenha(bCryptPasswordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        log.debug("Salvo no banco de dados usuario: {}", usuario);
    }

    private void usuarioExiste(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("usuario já existe com o e-mail");

            throw new UsuarioJaExisteComEmailException("Usuário com e-mail "
                    + usuario.getEmail() + " já cadastrado!");
        }
    }

    public Iterable<Usuario> obterTodosUsuarios() {
        log.info("Buscando todos usuario no banco de dados");
        Iterable<Usuario> usuarios = usuarioRepository.findAll();
        log.debug("usuarios: {}", usuarios);

        return usuarios;
    }

    public Usuario procurarUsuarioPeloId(UUID id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isEmpty()) {
            log.warn("Usuario não encontrado");

            throw new UsuarioNaoExisteException("O usuário com id " + id + " não existe!");
        }

        return optionalUsuario.get();
    }

    public void ativarOuDesativarUsuario(UUID id) {
        Usuario usuarioAtual = procurarUsuarioPeloId(id);
        log.info("Salvar dados no banco de dados");
        usuarioAtual.setAtivo(!usuarioAtual.getAtivo());
        usuarioRepository.save(usuarioAtual);
        log.debug("usuario: {}", usuarioAtual);
    }

    public Usuario atualizarNivelDeAcesso(UUID id, RolesEnum nivelDeAcesso) {
        Usuario usuario = procurarUsuarioPeloId(id);
        usuario.setNivelDeAcesso(nivelDeAcesso);
        log.info("salvar novo nivel do usuario no banco de dados");
        return usuarioRepository.save(usuario);
    }

    public Usuario procurarUsuarioPeloEmail(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);

        if (optionalUsuario.isEmpty()) {
            log.warn("usuario nao enocntrado pro email {}", email);

            throw new RuntimeException("Usuário com email " + email + " não localizado!");
        }

        return optionalUsuario.get();
    }
}
