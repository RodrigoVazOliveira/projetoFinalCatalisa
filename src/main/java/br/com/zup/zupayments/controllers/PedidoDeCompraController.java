package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.pedidodecompras.entrada.AtualizarResponsavelDoPedidoDeCompraDTO;
import br.com.zup.zupayments.dtos.pedidodecompras.entrada.EntradaCadastroPedidoDeCompraDTO;
import br.com.zup.zupayments.dtos.pedidodecompras.entrada.FiltroPedidoDeCompraComNotaFiscalPendenteDTO;
import br.com.zup.zupayments.dtos.pedidodecompras.saida.SaidaCadastroPedidoDeCompraDTO;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.services.PedidoDeCompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("pedidos/")
@Tag(name = "API REST Pedidos de compras")
public class PedidoDeCompraController {
    private final PedidoDeCompraService pedidoDeCompraService;

    public PedidoDeCompraController(PedidoDeCompraService pedidoDeCompraService) {
        this.pedidoDeCompraService = pedidoDeCompraService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar um novo pedido de compra", description = "Cadastrar um novo pedido de compra")
    SaidaCadastroPedidoDeCompraDTO cadastrarNovoPedidoDeCompra(@RequestBody @Valid EntradaCadastroPedidoDeCompraDTO cadastroPedidoDeCompraDTO) {
        PedidoDeCompra pedidoDeCompra = pedidoDeCompraService.cadastrarNovoPedidoDeCompra(cadastroPedidoDeCompraDTO.converterDtoParaModelo());

        return SaidaCadastroPedidoDeCompraDTO.converterModeloParaDto(pedidoDeCompra);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retornar todos os pedidos de compras cadastrados", description = "Retornar todos os pedidos de compras cadastrados")
    Iterable<SaidaCadastroPedidoDeCompraDTO> mostrarTodosPedidoDeCompra() {
        Iterable<PedidoDeCompra> pedidoDeCompras = pedidoDeCompraService.obterTodosOsPedidoDeCompra();

        return SaidaCadastroPedidoDeCompraDTO.converterListaDeModeloParaListaDto(pedidoDeCompras);
    }

    @PatchMapping("{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "cancela um pedido de compra pelo seu número de pedido de compra", description = "cancela um pedido de compra pelo seu número de pedido de compra")
    void cancelarPedidoDeCompra(@PathVariable Long id) {
        pedidoDeCompraService.cancelarPedidoDeCompra(id);
    }

    @GetMapping("responsaveis")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retorna todos os pedidos de compra com responsavel inativo", description = "Retorna todos os pedidos de compra com responsavel inativo")
    Iterable<PedidoDeCompra> obterTodosPedidosDeCompraComResponsavelInativo(@RequestParam(name = "ativo", defaultValue = "false") Boolean ativo) {
        return pedidoDeCompraService.obterTodosPedidosDeCompraComResponsavelAtivo(ativo);
    }

    @GetMapping("pendentes")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retornar todos os pedidos de compra com nota fiscal nao enviadas", description = "Retornar todos os pedidos de compra com nota fiscal nao enviadas")
    Iterable<PedidoDeCompra> obterPedidosComNotaFiscaisPendentesDeEnvio(@ModelAttribute FiltroPedidoDeCompraComNotaFiscalPendenteDTO filtro) {
        return pedidoDeCompraService
                .obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(
                        filtro.valorMinimo(),
                        filtro.ativo(),
                        filtro.dataInicial()
                );
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Atualizar o pedido de compra com responsavel inativo")
    void alterarResponsavelDoPedido(@RequestBody AtualizarResponsavelDoPedidoDeCompraDTO atualizarResponsavelDoPedidoDeCompraDTO) {
        pedidoDeCompraService.atualizarResponsavelPorPedidoDeCompra(atualizarResponsavelDoPedidoDeCompraDTO);
    }

    @GetMapping("cobrancas")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Envia e-mail para o responsável do pedido de compra que possuem pendencia de nota fiscal")
    void enviarEmailDeCobrancas(@ModelAttribute FiltroPedidoDeCompraComNotaFiscalPendenteDTO filtro) {
        try {
            final Double valorMinimo = filtro.valorMinimo();
            final Boolean ativo = filtro.ativo();
            final LocalDate dataInicial = filtro.dataInicial();

            pedidoDeCompraService.enviarEmailParaPedidosDeCompraComNotasPendentes(valorMinimo, ativo, dataInicial);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}