package br.com.zup.zupayments.services;

import br.com.zup.zupayments.exceptions.erros.NotaFiscalNaoCadastradaException;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.repositories.NotaFiscalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final ResponsavelService responsavelService;
    private final FornecedorService fornecedorService;
    private final PedidoDeCompraService pedidoDeCompraService;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, ResponsavelService responsavelService, FornecedorService fornecedorService, PedidoDeCompraService pedidoDeCompraService) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.responsavelService = responsavelService;
        this.fornecedorService = fornecedorService;
        this.pedidoDeCompraService = pedidoDeCompraService;
    }

    public NotaFiscal cadastrarNotaFiscal(NotaFiscal fiscal) {
        fiscal.setResponsavel(responsavelService.procurarResponsavelPorEmail(fiscal.getResponsavel().getEmail()));
        fiscal.setFornecedor(fornecedorService.pesquisarFornecedorPorCnpjOuCpf(fiscal.getFornecedor().getCnpjOuCpf()));
        fiscal.setPedidoDeCompra(gerarListaDePedidoDeCompraParaCadastrar(fiscal.getPedidoDeCompra()));
        NotaFiscal novaNotaFiscal = notaFiscalRepository.save(fiscal);
        pedidoDeCompraService.debitarValorDaNotaFiscalNoPedido(novaNotaFiscal);
        return novaNotaFiscal;
    }

    private List<PedidoDeCompra> gerarListaDePedidoDeCompraParaCadastrar(List<PedidoDeCompra> pedidoDeCompras) {
        List<PedidoDeCompra> listaDePedidoDeCompraParaCadastrar = new ArrayList<>();

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            listaDePedidoDeCompraParaCadastrar.add(pedidoDeCompraService.procurarPedidoDeCompraPeloNumeroDePedido(pedidoDeCompra.getNumeroDePedido()));
        }

        return listaDePedidoDeCompraParaCadastrar;
    }

    public NotaFiscal pesquisarNotaFiscalPeloId(Long id) {
        Optional<NotaFiscal> optionalNotaFiscal = notaFiscalRepository.findById(id);

        if (optionalNotaFiscal.isEmpty()) {
            throw new NotaFiscalNaoCadastradaException("Nota fiscal não esta cadastrada");
        }
        return optionalNotaFiscal.get();
    }

    public NotaFiscal cancelarNF(Long id) {
        NotaFiscal notaFiscalAtual = pesquisarNotaFiscalPeloId(id);
        notaFiscalAtual.setCancelar(!notaFiscalAtual.getCancelar());
        return notaFiscalRepository.save(notaFiscalAtual);
    }

    public Iterable<NotaFiscal> obterTodasNotaFiscalComIntervaloDeDataDeEmissao(LocalDate dataInicial, LocalDate dataFinal) {
        return notaFiscalRepository.findAllByDataDeEmissaoBetween(dataInicial, dataFinal);
    }
}