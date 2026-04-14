package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.core.ampper.CadastrarResponsavelDTOToResponsavelMapper;
import br.com.zup.zupayments.dtos.responsavel.entrada.CadastrarResponsavelDTO;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.services.ResponsavelService;
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


@RestController
@RequestMapping("responsaveis/")
@Tag(name = "Responsáveis", description = "Endpoints para gerenciar responsáveis de projetos")
public class ResponsavelController {
    private static final Logger log = LoggerFactory.getLogger(ResponsavelController.class);
    private final ResponsavelService responsavelService;
    private final CadastrarResponsavelDTOToResponsavelMapper mapper;

    public ResponsavelController(ResponsavelService responsavelService,
                                 CadastrarResponsavelDTOToResponsavelMapper mapper) {
        this.responsavelService = responsavelService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo responsável",
               description = "Cria um novo responsável no sistema com os dados fornecidos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Responsável cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Responsavel.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou email já cadastrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public Responsavel cadastrarResponsavel(
            @RequestBody
            @Valid CadastrarResponsavelDTO cadastrarResponsavelDTO) {
        log.info("Iniciando cadastro de responsável através do controller");
        Responsavel responsavel = mapper.map(cadastrarResponsavelDTO);

        return responsavelService.cadastrarResponsavel(responsavel);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativar ou desativar responsável",
               description = "Alterna o status (ativo/inativo) de um responsável pelo email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "404",
                    description = "Responsável não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public void ativarOuDesativarResponsavel(
            @Parameter(description = "Email do responsável", required = true, example = "responsavel@example.com")
            @RequestParam(name = "email") String emailResponsavel) {
        log.info("Iniciando ativação/desativação de responsável com email: {}", emailResponsavel);

        responsavelService.ativarOuDesativarResponsavel(emailResponsavel);
    }
}
