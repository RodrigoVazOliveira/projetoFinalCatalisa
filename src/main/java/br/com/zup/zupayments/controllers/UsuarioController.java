package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.core.ampper.CadastroUsuarioDTOToEntity;
import br.com.zup.zupayments.core.ampper.UsuarioParaUsuarioDTO;
import br.com.zup.zupayments.dtos.usuario.entrada.CadastrarUsuarioDTO;
import br.com.zup.zupayments.dtos.usuario.entrada.NivelDeAcessoUsuarioDTO;
import br.com.zup.zupayments.dtos.usuario.saida.UsuarioDTO;
import br.com.zup.zupayments.models.Usuario;
import br.com.zup.zupayments.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("usuarios/")
@Tag(name = "API REST de usuários")
public class UsuarioController {
    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;
    private final CadastroUsuarioDTOToEntity cadastroUsuarioDTOToEntity;
    private final UsuarioParaUsuarioDTO converterModeloParaDTO;

    public UsuarioController(UsuarioService usuarioService,
                             CadastroUsuarioDTOToEntity cadastroUsuarioDTOToEntity,
                             UsuarioParaUsuarioDTO converterModeloParaDTO) {
        this.usuarioService = usuarioService;
        this.cadastroUsuarioDTOToEntity = cadastroUsuarioDTOToEntity;
        this.converterModeloParaDTO = converterModeloParaDTO;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar um novo usuário")
    void cadastrarNovoUsuario(@RequestBody @Valid
                              CadastrarUsuarioDTO cadastrarUsuarioDTO) {
        log.info("Cadastrando usuario");
        Usuario usuario = cadastroUsuarioDTOToEntity.map(cadastrarUsuarioDTO);
        usuarioService.cadastrarNovoUsuario(usuario);
        log.info("Cadastrando usuario realizado com sucesso");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Mostrar todos os usuários")
    Iterable<UsuarioDTO> mostarTodosUsuarios() {
        log.info("Buscando todos os usuarios cadastrados");
        Iterable<Usuario> usuarios = usuarioService.obterTodosUsuarios();
        List<UsuarioDTO> usuarioDTOs = new ArrayList<>();
        usuarios.forEach(usuario -> usuarioDTOs.add(converterModeloParaDTO.map(usuario)));

        return usuarioDTOs;
    }

    @PatchMapping("ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativar ou desativar um usuário pelo id")
    void ativarOuDesativarUsuario(@RequestParam(name = "idUsuario") UUID id) {
        log.info("Mudar estado do usuario: {}", id);
        usuarioService.ativarOuDesativarUsuario(id);
    }

    @PatchMapping("nivel")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Alterar o nível de acesso do usuário")
    UsuarioDTO atualizarNivelDeAcesso(@RequestParam(name = "idUsuario")
                                      UUID id,
                                      @RequestBody @Valid
                                      NivelDeAcessoUsuarioDTO nivelDeAcessoUsuarioDTO) {
        log.info("Atualizar nivel de acesso");
        Usuario usuario = usuarioService.atualizarNivelDeAcesso(
                id,
                nivelDeAcessoUsuarioDTO.nivelDeAcesso()
        );

        return converterModeloParaDTO.map(usuario);
    }
}
