package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.pedidodecompras.entrada.AtualizarResponsavelDoPedidoDeCompraDTO;
import br.com.zup.zupayments.dtos.pedidodecompras.entrada.EntradaCadastroPedidoDeCompraDTO;
import br.com.zup.zupayments.dtos.pedidodecompras.entrada.FiltroPedidoDeCompraComNotaFiscalPendenteDTO;
import br.com.zup.zupayments.dtos.pedidodecompras.saida.SaidaCadastroPedidoDeCompraDTO;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.services.PedidoDeCompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("pedidos/")
@Tag(name = "Pedidos de Compra", description = "Endpoints para gerenciar pedidos de compra")
public class PedidoDeCompraController {
    private static final Logger log = LoggerFactory.getLogger(PedidoDeCompraController.class);
    private final PedidoDeCompraService pedidoDeCompraService;

    public PedidoDeCompraController(PedidoDeCompraService pedidoDeCompraService) {
        this.pedidoDeCompraService = pedidoDeCompraService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo pedido de compra",
               description = "Cria um novo pedido de compra no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                    description = "Pedido de compra cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = SaidaCadastroPedidoDeCompraDTO.class))),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    SaidaCadastroPedidoDeCompraDTO cadastrarNovoPedidoDeCompra(
            @RequestBody @Valid EntradaCadastroPedidoDeCompraDTO cadastroPedidoDeCompraDTO) {
        log.info("Iniciando cadastro de novo pedido de compra");
        PedidoDeCompra pedidoDeCompra = pedidoDeCompraService.cadastrarNovoPedidoDeCompra(
                cadastroPedidoDeCompraDTO.converterDtoParaModelo()
        );
        log.info("Pedido de compra cadastrado com sucesso. Número: {}", pedidoDeCompra.getNumeroDePedido());
        return SaidaCadastroPedidoDeCompraDTO.converterModeloParaDto(pedidoDeCompra);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar todos os pedidos de compra",
               description = "Retorna todos os pedidos de compra cadastrados no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Lista de pedidos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = SaidaCadastroPedidoDeCompraDTO.class))),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    Iterable<SaidaCadastroPedidoDeCompraDTO> mostrarTodosPedidoDeCompra() {
        log.info("Buscando todos os pedidos de compra");
        Iterable<PedidoDeCompra> pedidoDeCompras = pedidoDeCompraService.obterTodosOsPedidoDeCompra();
        log.info("Busca de pedidos de compra realizada com sucesso");
        return SaidaCadastroPedidoDeCompraDTO.converterListaDeModeloParaListaDto(pedidoDeCompras);
    }

    @PatchMapping("{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancelar pedido de compra",
               description = "Cancela um pedido de compra pelo seu número")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Pedido cancelado com sucesso"),
        @ApiResponse(responseCode = "404",
                    description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    void cancelarPedidoDeCompra(
            @Parameter(description = "ID do pedido de compra", required = true, example = "1")
            @PathVariable UUID id) {
        log.info("Iniciando cancelamento do pedido de compra: {}", id);
        pedidoDeCompraService.cancelarPedidoDeCompra(id);
        log.info("Pedido de compra cancelado com sucesso. ID: {}", id);
    }

    @GetMapping("responsaveis")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar pedidos com responsável inativo",
               description = "Retorna todos os pedidos de compra cujo responsável está inativo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Lista de pedidos retornada com sucesso"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    Iterable<PedidoDeCompra> obterTodosPedidosDeCompraComResponsavelInativo(
            @Parameter(description = "Status do responsável (true/false)", example = "false")
            @RequestParam(name = "ativo", defaultValue = "false") Boolean ativo) {
        log.info("Buscando pedidos de compra com responsável ativo={}", ativo);
        Iterable<PedidoDeCompra> pedidos = pedidoDeCompraService.obterTodosPedidosDeCompraComResponsavelAtivo(ativo);
        log.info("Busca realizada com sucesso");
        return pedidos;
    }

    @GetMapping("pendentes")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar pedidos com notas fiscais pendentes",
               description = "Retorna todos os pedidos de compra com notas fiscais não enviadas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Lista de pedidos retornada com sucesso"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    Iterable<PedidoDeCompra> obterPedidosComNotaFiscaisPendentesDeEnvio(
            @ModelAttribute FiltroPedidoDeCompraComNotaFiscalPendenteDTO filtro) {
        log.info("Buscando pedidos com notas fiscais pendentes. Filtro: valor mínimo={}, ativo={}, data inicial={}",
                filtro.valorMinimo(), filtro.ativo(), filtro.dataInicial());

        Iterable<PedidoDeCompra> pedidos = pedidoDeCompraService
                .obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(
                        filtro.valorMinimo(),
                        filtro.ativo(),
                        filtro.dataInicial()
                );

        log.info("Busca de pedidos pendentes realizada com sucesso");
        return pedidos;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Atualizar responsável do pedido",
               description = "Atualiza o responsável de um pedido de compra")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
                    description = "Responsável atualizado com sucesso"),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos"),
        @ApiResponse(responseCode = "404",
                    description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "500",
                    description = "Erro interno do servidor")
    })
    void alterarResponsavelDoPedido(
            @RequestBody AtualizarResponsavelDoPedidoDeCompraDTO atualizarResponsavelDoPedidoDeCompraDTO) {
        log.info("Iniciando atualização de responsável. Pedido: {}, Email: {}",
                atualizarResponsavelDoPedidoDeCompraDTO.numeroPedidoDeCompra(),
                atualizarResponsavelDoPedidoDeCompraDTO.emailResponsavel());

        pedidoDeCompraService.atualizarResponsavelPorPedidoDeCompra(atualizarResponsavelDoPedidoDeCompraDTO);

        log.info("Responsável atualizado com sucesso. Pedido: {}",
                atualizarResponsavelDoPedidoDeCompraDTO.numeroPedidoDeCompra());
    }

    @GetMapping("cobrancas")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Enviar emails de cobrança",
               description = "Envia e-mails para responsáveis com pedidos pendentes de nota fiscal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Emails enviados com sucesso"),
        @ApiResponse(responseCode = "500",
                    description = "Erro ao enviar emails")
    })
    void enviarEmailDeCobrancas(
            @ModelAttribute FiltroPedidoDeCompraComNotaFiscalPendenteDTO filtro) {
        try {
            log.info("Iniciando envio de emails de cobrança. Filtro: valor mínimo={}, ativo={}, data inicial={}",
                    filtro.valorMinimo(), filtro.ativo(), filtro.dataInicial());

            final Double valorMinimo = filtro.valorMinimo();
            final Boolean ativo = filtro.ativo();
            final LocalDate dataInicial = filtro.dataInicial();

            pedidoDeCompraService.enviarEmailParaPedidosDeCompraComNotasPendentes(valorMinimo, ativo, dataInicial);

            log.info("Emails de cobrança enviados com sucesso");
        } catch (MessagingException e) {
            log.error("Erro ao enviar emails de cobrança", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}