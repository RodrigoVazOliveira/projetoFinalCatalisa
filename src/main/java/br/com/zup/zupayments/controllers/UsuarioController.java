package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.core.ampper.CadastroUsuarioDTOToEntity;
import br.com.zup.zupayments.core.ampper.UsuarioParaUsuarioDTO;
import br.com.zup.zupayments.dtos.usuario.entrada.CadastrarUsuarioDTO;
import br.com.zup.zupayments.dtos.usuario.entrada.NivelDeAcessoUsuarioDTO;
import br.com.zup.zupayments.dtos.usuario.saida.UsuarioDTO;
import br.com.zup.zupayments.models.Usuario;
import br.com.zup.zupayments.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Usuários", description = "Endpoints para gerenciar usuários do sistema")
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
    @Operation(summary = "Cadastrar novo usuário",
               description = "Cria um novo usuário no sistema com os dados fornecidos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Usuário cadastrado com sucesso"),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou usuário já existe"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    void cadastrarNovoUsuario(
            @RequestBody
            @Valid CadastrarUsuarioDTO cadastrarUsuarioDTO) {
        log.info("Cadastrando usuario");
        Usuario usuario = cadastroUsuarioDTOToEntity.map(cadastrarUsuarioDTO);
        usuarioService.cadastrarNovoUsuario(usuario);
        log.info("Cadastrando usuario realizado com sucesso");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar todos os usuários",
               description = "Retorna uma lista com todos os usuários cadastrados no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Lista de usuários recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = UsuarioDTO.class))),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    Iterable<UsuarioDTO> mostarTodosUsuarios() {
        log.info("Buscando todos os usuarios cadastrados");
        Iterable<Usuario> usuarios = usuarioService.obterTodosUsuarios();
        List<UsuarioDTO> usuarioDTOs = new ArrayList<>();
        usuarios.forEach(usuario -> usuarioDTOs.add(converterModeloParaDTO.map(usuario)));

        return usuarioDTOs;
    }

    @PatchMapping("ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativar ou desativar usuário",
               description = "Alterna o status (ativo/inativo) de um usuário pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    void ativarOuDesativarUsuario(
            @Parameter(description = "ID único do usuário", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(name = "idUsuario") UUID id) {
        log.info("Mudar estado do usuario: {}", id);
        usuarioService.ativarOuDesativarUsuario(id);
    }

    @PatchMapping("nivel")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Atualizar nível de acesso do usuário",
               description = "Altera o nível de acesso (permissões) de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Nível de acesso alterado com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = UsuarioDTO.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos"),
        @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    UsuarioDTO atualizarNivelDeAcesso(
            @Parameter(description = "ID único do usuário", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(name = "idUsuario") UUID id,
            @RequestBody
            @Valid NivelDeAcessoUsuarioDTO nivelDeAcessoUsuarioDTO) {
        log.info("Atualizar nivel de acesso");
        Usuario usuario = usuarioService.atualizarNivelDeAcesso(
                id,
                nivelDeAcessoUsuarioDTO.nivelDeAcesso()
        );

        return converterModeloParaDTO.map(usuario);
    }
}
