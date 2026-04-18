package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.notafiscal.entrada.CadastrarNotaFiscalDTO;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.services.NotaFiscalService;
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

import java.util.UUID;

/**
 * REST Controller para gerenciar Notas Fiscais
 */
@RestController
@RequestMapping("notas_fiscais/")
@Tag(name = "Notas Fiscais", description = "Endpoints para gerenciar notas fiscais do sistema")
public class NotaFiscalController {
    private static final Logger log = LoggerFactory.getLogger(NotaFiscalController.class);
    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar nova nota fiscal",
               description = "Cria uma nova nota fiscal no sistema com os dados fornecidos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Nota fiscal cadastrada com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = NotaFiscal.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou validação falhou"),
        @ApiResponse(responseCode = "404",
                    description = "Fornecedor ou responsável não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public NotaFiscal cadastroNotaFiscal(
            @RequestBody
            @Valid CadastrarNotaFiscalDTO cadastrarNotaFiscalDTO) {
        log.info("Iniciando cadastro de nova nota fiscal. Número: {}, Fornecedor CNPJ/CPF: {}",
                cadastrarNotaFiscalDTO.numeroDaNota(),
                cadastrarNotaFiscalDTO.cnpjOuCpfFornecedor()
        );

        NotaFiscal notaFiscal = notaFiscalService.cadastrarNotaFiscal(
                cadastrarNotaFiscalDTO.converterDtoParaModelo()
        );

        log.info("Nota fiscal cadastrada com sucesso. ID: {}, Número: {}, Valor: R$ {}",
                notaFiscal.getId(),
                notaFiscal.getNumeroDaNota(),
                notaFiscal.getValorAPagar()
        );

        return notaFiscal;
    }

    @PatchMapping("{id}/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancelar nota fiscal",
               description = "Cancela uma nota fiscal existente pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Nota fiscal cancelada com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = NotaFiscal.class))),
        @ApiResponse(responseCode = "404",
                    description = "Nota fiscal não encontrada"),
        @ApiResponse(responseCode = "409",
                    description = "Nota fiscal já está cancelada"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    public NotaFiscal cancelamentoDeNotaFiscal(
            @Parameter(description = "ID da nota fiscal a ser cancelada", required = true, example = "1")
            @PathVariable UUID id) {
        log.info("Iniciando cancelamento de nota fiscal. ID: {}", id);

        NotaFiscal notaFiscal = notaFiscalService.cancelarNF(id);

        log.info("Nota fiscal cancelada com sucesso. ID: {}, Número: {}, Valor: R$ {}",
                notaFiscal.getId(), notaFiscal.getNumeroDaNota(), notaFiscal.getValorAPagar());

        return notaFiscal;
    }
}
