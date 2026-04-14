package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.fornecedor.entrada.AtualizarFornecedorDTO;
import br.com.zup.zupayments.dtos.fornecedor.entrada.CadastroDeFornecedorDTO;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.services.FornecedorService;
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
@RequestMapping("fornecedores/")
@Tag(name = "Fornecedores", description = "Endpoints para gerenciar fornecedores do sistema")
public class FornecedorController {
    private static final Logger log = LoggerFactory.getLogger(FornecedorController.class);
    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo fornecedor",
               description = "Cria um novo fornecedor no sistema com os dados fornecidos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Fornecedor cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Fornecedor.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou fornecedor já existe"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public Fornecedor cadastrarFornecedor(
            @RequestBody
            @Valid CadastroDeFornecedorDTO cadastroDeFornecedorDTO) {
        log.info("Cadastrando novo fornecedor");
        return fornecedorService.cadastrarFornecedor(cadastroDeFornecedorDTO.converterDtoParaModelo());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Pesquisar fornecedor",
               description = "Busca um fornecedor no sistema pelo CNPJ ou CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Fornecedor encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Fornecedor.class))),
        @ApiResponse(responseCode = "404",
                    description = "Fornecedor não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public Fornecedor pesquisarFornecedor(
            @Parameter(description = "CNPJ ou CPF do fornecedor", required = true, example = "12345678000195")
            @RequestParam(value = "cnpjOuCpf") String cnpjouCpf) {
        log.info("Pesquisando fornecedor com CNPJ/CPF: {}", cnpjouCpf);
        return fornecedorService.pesquisarFornecedorPorCnpjOuCpf(cnpjouCpf);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Atualizar fornecedor",
               description = "Atualiza os dados de um fornecedor existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Fornecedor atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Fornecedor.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos"),
        @ApiResponse(responseCode = "404",
                    description = "Fornecedor não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public Fornecedor atualizarFornecedor(
            @RequestBody
            @Valid AtualizarFornecedorDTO atualizarFornecedorDTO) {
        log.info("Atualizando fornecedor");
        return fornecedorService.atualizarCadastroFornecedor(atualizarFornecedorDTO.converterDtoParaModelo());
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativar ou desativar fornecedor",
               description = "Alterna o status (ativo/inativo) de um fornecedor pelo CNPJ ou CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "404",
                    description = "Fornecedor não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public void ativarOuDesativarFornecedorPeloCnpjOuCpf(
            @Parameter(description = "CNPJ ou CPF do fornecedor", required = true, example = "12345678000195")
            @RequestParam(name = "cnpjoucpf") String cnpjOuCpf) {
        log.info("Alternando status do fornecedor com CNPJ/CPF: {}", cnpjOuCpf);
        fornecedorService.ativarOuDesativarFornecedor(cnpjOuCpf);
    }
}