package br.com.zup.zupayments.services;

import br.com.zup.zupayments.dtos.pedidodecompras.entrada.AtualizarResponsavelDoPedidoDeCompraDTO;
import br.com.zup.zupayments.exceptions.erros.PedidoDeCompraNaoExisteException;
import br.com.zup.zupayments.exceptions.erros.PedidoDeCompraSemSaldoException;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.PedidoDeCompraRespository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoDeCompraService {

    private final PedidoDeCompraRespository pedidoDeCompraRespository;
    private final ResponsavelService responsavelService;
    private final FornecedorService fornecedorService;
    private final NotaFiscalService notaFiscalService;
    private final EmailService emailService;

    @Autowired
    @Lazy
    public PedidoDeCompraService(PedidoDeCompraRespository pedidoDeCompraRespository,
                                 ResponsavelService responsavelService,
                                 FornecedorService fornecedorService,
                                 NotaFiscalService notaFiscalService,
                                 EmailService emailService) {
        this.pedidoDeCompraRespository = pedidoDeCompraRespository;
        this.responsavelService = responsavelService;
        this.fornecedorService = fornecedorService;
        this.notaFiscalService = notaFiscalService;
        this.emailService = emailService;
    }

    public PedidoDeCompra cadastrarNovoPedidoDeCompra(PedidoDeCompra pedidoDeCompra) {
        pedidoDeCompra.setResponsavel(responsavelService.procurarResponsavelPorEmail(pedidoDeCompra.getResponsavel().getEmail()));
        pedidoDeCompra.setFornecedor(fornecedorService.pesquisarFornecedorPorCnpjOuCpf(pedidoDeCompra.getFornecedor().getCnpjOuCpf()));
        return pedidoDeCompraRespository.save(pedidoDeCompra);
    }

    public Iterable<PedidoDeCompra> obterTodosOsPedidoDeCompra() {
        return pedidoDeCompraRespository.findAll();
    }

    public PedidoDeCompra procurarPedidoDeCompraPeloNumeroDePedido(Long numeroPedidoDeCompra) {
        Optional<PedidoDeCompra> optionalPedidoDeCompra = pedidoDeCompraRespository.findById(numeroPedidoDeCompra);

        if (optionalPedidoDeCompra.isEmpty()) {
            throw new PedidoDeCompraNaoExisteException("Não há pedido cadastrado com este número" + numeroPedidoDeCompra);
        }

        return optionalPedidoDeCompra.get();
    }

    public void cancelarPedidoDeCompra(Long id) {
        PedidoDeCompra pedidoDeCompraOptional = procurarPedidoDeCompraPeloNumeroDePedido(id);
        pedidoDeCompraOptional.setCancelado(true);
        pedidoDeCompraRespository.save(pedidoDeCompraOptional);
    }

    public PedidoDeCompra cancelarPedidoDeCompra(Long id, PedidoDeCompra pedidoDeCompra) {
        Optional<PedidoDeCompra> pedidoDeCompraOptional = pedidoDeCompraRespository.findById(id);

        if (pedidoDeCompraOptional.isEmpty())
            throw new PedidoDeCompraNaoExisteException("Pedido de compra não cadastrado");

        pedidoDeCompra.setCancelado(true);
        pedidoDeCompraRespository.save(pedidoDeCompra);

        return pedidoDeCompra;
    }

    public Iterable<PedidoDeCompra> obterTodosPedidosDeCompraComResponsavelAtivo(Boolean ativo) {
        return pedidoDeCompraRespository.findAllByResponsavelAtivo(ativo);
    }

    public Iterable<PedidoDeCompra> obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(Double valorMinimo, Boolean ativo, LocalDate dataInicial) {
        Iterable<NotaFiscal> notasFiscais = notaFiscalService.obterTodasNotaFiscalComIntervaloDeDataDeEmissao(dataInicial, LocalDate.now());
        Iterable<PedidoDeCompra> pedidoDeCompras = pedidoDeCompraRespository.findAllBySaldoGreaterThanAndResponsavelAtivo(valorMinimo, ativo);


        return verificarPendenciasDeNotaFiscal((List<NotaFiscal>) notasFiscais, (List<PedidoDeCompra>) pedidoDeCompras);
    }

    private Iterable<PedidoDeCompra> verificarPendenciasDeNotaFiscal(List<NotaFiscal> notaFiscals, List<PedidoDeCompra> pedidoDeCompras) {
        List<PedidoDeCompra> removeListaDePedidos = new ArrayList<>();

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            for (NotaFiscal notaFiscal : notaFiscals) {
                if (notaFiscal.getDataDeEmissao().getMonthValue() == LocalDate.now().getMonthValue() && notaFiscal.getPedidoDeCompra().contains(pedidoDeCompra)) {
                    removeListaDePedidos.add(pedidoDeCompra);
                }
            }
        }

        for (PedidoDeCompra remove : removeListaDePedidos) {
            pedidoDeCompras.remove(remove);
        }

        return pedidoDeCompras;
    }

    public void atualizarResponsavelPorPedidoDeCompra(AtualizarResponsavelDoPedidoDeCompraDTO dados) {
        PedidoDeCompra pedidoDeCompra =
                procurarPedidoDeCompraPeloNumeroDePedido(dados.numeroPedidoDeCompra());
        Responsavel responsavel =
                responsavelService.procurarResponsavelPorEmail(dados.emailResponsavel());
        pedidoDeCompra.setResponsavel(responsavel);
        pedidoDeCompraRespository.save(pedidoDeCompra);
    }

    public void enviarEmailParaPedidosDeCompraComNotasPendentes(Double valorMinimo, Boolean ativo, LocalDate dataInicial) throws MessagingException {
        Iterable<PedidoDeCompra> pedidoDeCompras = obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(valorMinimo, ativo, dataInicial);
        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            emailService.enviarEmailDePedidoPendenteDeNotaFiscal(pedidoDeCompra);
        }
    }

    public void debitarValorDaNotaFiscalNoPedido(NotaFiscal nf) {
        Double saldoTotal = somarValoresDosPedidos(nf.getPedidoDeCompra());

        if (saldoTotal < nf.getValorAPagar()) {
            throw new PedidoDeCompraSemSaldoException("Pedido não possui saldo sufuciente");
        }

        Double valorNota = nf.getValorAPagar();
        for (PedidoDeCompra pedidoDeCompra : nf.getPedidoDeCompra()) {
            valorNota = atualizarPedido(pedidoDeCompra, valorNota);
        }

    }

    private Double somarValoresDosPedidos(List<PedidoDeCompra> pedidoDeCompras) {
        Double soma = 0.0;

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            soma += pedidoDeCompra.getSaldo();
        }

        return soma;
    }

    private Double atualizarPedido(PedidoDeCompra pedidoDeCompra, Double valor) {
        PedidoDeCompra pedidoBancoDeDados = procurarPedidoDeCompraPeloNumeroDePedido(pedidoDeCompra.getNumeroDePedido());
        Double auxiliar = pedidoBancoDeDados.getSaldo();

        if (auxiliar >= valor) {
            pedidoBancoDeDados.setSaldo(auxiliar - valor);
            pedidoDeCompraRespository.save(pedidoBancoDeDados);
            return 0.0;
        } else {
            Double valorNota = valor - auxiliar;
            pedidoBancoDeDados.setSaldo(valor - auxiliar);
            pedidoDeCompraRespository.save(pedidoBancoDeDados);
            return valorNota;
        }
    }
}
