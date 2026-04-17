package br.com.zup.zupayments.services;

import br.com.zup.zupayments.dtos.pedidodecompras.entrada.AtualizarResponsavelDoPedidoDeCompraDTO;
import br.com.zup.zupayments.exceptions.erros.PedidoDeCompraNaoExisteException;
import br.com.zup.zupayments.exceptions.erros.PedidoDeCompraSemSaldoException;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.PedidoDeCompraRespository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PedidoDeCompraService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoDeCompraService.class);

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
        logger.info("Iniciando cadastro de novo pedido de compra");
        pedidoDeCompra.setResponsavel(responsavelService.procurarResponsavelPorEmail(pedidoDeCompra.getResponsavel().getEmail()));
        pedidoDeCompra.setFornecedor(fornecedorService.pesquisarFornecedorPorCnpjOuCpf(pedidoDeCompra.getFornecedor().getCnpjOuCpf()));
        PedidoDeCompra pedidoSalvo = pedidoDeCompraRespository.save(pedidoDeCompra);
        logger.info("Pedido de compra cadastrado com sucesso - Número: {}, Responsável: {}, Fornecedor: {}",
            pedidoSalvo.getNumeroDePedido(), pedidoSalvo.getResponsavel().getEmail(), pedidoSalvo.getFornecedor().getCnpjOuCpf());
        return pedidoSalvo;
    }

    public Iterable<PedidoDeCompra> obterTodosOsPedidoDeCompra() {
        logger.debug("Obtendo todos os pedidos de compra");
        return pedidoDeCompraRespository.findAll();
    }

    public PedidoDeCompra procurarPedidoDeCompraPeloNumeroDePedido(UUID numeroPedidoDeCompra) {
        logger.debug("Pesquisando pedido de compra com número: {}",
                numeroPedidoDeCompra);
        Optional<PedidoDeCompra> optionalPedidoDeCompra = pedidoDeCompraRespository.findById(numeroPedidoDeCompra);

        if (optionalPedidoDeCompra.isEmpty()) {
            logger.warn("Pedido de compra não encontrado: {}", numeroPedidoDeCompra);
            throw new PedidoDeCompraNaoExisteException("Não há pedido cadastrado com este número: " + numeroPedidoDeCompra);
        }

        logger.debug("Pedido de compra encontrado: {}", numeroPedidoDeCompra);
        return optionalPedidoDeCompra.get();
    }

    public void cancelarPedidoDeCompra(UUID id) {
        logger.info("Iniciando cancelamento de pedido de compra: {}", id);
        PedidoDeCompra pedidoDeCompraOptional = procurarPedidoDeCompraPeloNumeroDePedido(id);
        pedidoDeCompraOptional.setCancelado(true);
        pedidoDeCompraRespository.save(pedidoDeCompraOptional);
        logger.info("Pedido de compra cancelado com sucesso: {}", id);
    }

    public PedidoDeCompra cancelarPedidoDeCompra(UUID id,
                                                 PedidoDeCompra pedidoDeCompra) {
        logger.info("Iniciando cancelamento de pedido de compra por objeto: {}", id);
        Optional<PedidoDeCompra> pedidoDeCompraOptional = pedidoDeCompraRespository.findById(id);

        if (pedidoDeCompraOptional.isEmpty()) {
            logger.warn("Pedido de compra não encontrado para cancelamento: {}", id);
            throw new PedidoDeCompraNaoExisteException("Pedido de compra não cadastrado");
        }

        pedidoDeCompra.setCancelado(true);
        pedidoDeCompraRespository.save(pedidoDeCompra);
        logger.info("Pedido de compra cancelado com sucesso por objeto: {}", id);
        return pedidoDeCompra;
    }

    public Iterable<PedidoDeCompra> obterTodosPedidosDeCompraComResponsavelAtivo(Boolean ativo) {
        logger.debug("Obtendo todos os pedidos com responsáveis ativos: {}", ativo);
        return pedidoDeCompraRespository.findAllByResponsavelAtivo(ativo);
    }

    public Iterable<PedidoDeCompra> obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(Double valorMinimo, Boolean ativo, LocalDate dataInicial) {
        logger.debug("Obtendo pedidos com valor mínimo: {}, responsáveis ativos: {} a partir de: {}", valorMinimo, ativo, dataInicial);
        List<NotaFiscal> notasFiscais = (List<NotaFiscal>) notaFiscalService.obterTodasNotaFiscalComIntervaloDeDataDeEmissao(dataInicial, LocalDate.now());
        List<PedidoDeCompra> pedidoDeCompras = (List<PedidoDeCompra>) pedidoDeCompraRespository.findAllBySaldoGreaterThanAndResponsavelAtivo(valorMinimo, ativo);

        return verificarPendenciasDeNotaFiscal(notasFiscais, pedidoDeCompras);
    }

    private Iterable<PedidoDeCompra> verificarPendenciasDeNotaFiscal(List<NotaFiscal> notaFiscals, List<PedidoDeCompra> pedidoDeCompras) {
        logger.debug("Verificando pendências de notas fiscais. Notas: {}, Pedidos: {}", notaFiscals.size(), pedidoDeCompras.size());
        List<PedidoDeCompra> removeListaDePedidos = new ArrayList<>();
        int mesAtual = LocalDate.now().getMonthValue();

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            for (NotaFiscal notaFiscal : notaFiscals) {
                if (notaFiscal.getDataDeEmissao().getMonthValue() == mesAtual && notaFiscal.getPedidoDeCompra().contains(pedidoDeCompra)) {
                    logger.debug("Pedido com nota fiscal pendente encontrado: {}", pedidoDeCompra.getNumeroDePedido());
                    removeListaDePedidos.add(pedidoDeCompra);
                }
            }
        }

        pedidoDeCompras.removeAll(removeListaDePedidos);
        logger.debug("Pedidos com pendências removidos: {}. Retornando: {} pedidos", removeListaDePedidos.size(), pedidoDeCompras.size());
        return pedidoDeCompras;
    }

    public void atualizarResponsavelPorPedidoDeCompra(AtualizarResponsavelDoPedidoDeCompraDTO dados) {
        logger.info("Atualizando responsável do pedido: {}. Novo email: {}", dados.numeroPedidoDeCompra(), dados.emailResponsavel());
        PedidoDeCompra pedidoDeCompra = procurarPedidoDeCompraPeloNumeroDePedido(dados.numeroPedidoDeCompra());
        Responsavel responsavel = responsavelService.procurarResponsavelPorEmail(dados.emailResponsavel());
        pedidoDeCompra.setResponsavel(responsavel);
        pedidoDeCompraRespository.save(pedidoDeCompra);
        logger.info("Responsável do pedido {} atualizado com sucesso para: {}", dados.numeroPedidoDeCompra(), dados.emailResponsavel());
    }

    public void enviarEmailParaPedidosDeCompraComNotasPendentes(Double valorMinimo, Boolean ativo, LocalDate dataInicial) throws MessagingException {
        logger.info("Iniciando envio de emails para pedidos com notas fiscais pendentes. Valor mínimo: {}, Data: {}", valorMinimo, dataInicial);
        Iterable<PedidoDeCompra> pedidoDeCompras = obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(valorMinimo, ativo, dataInicial);
        int contador = 0;
        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            try {
                emailService.enviarEmailDePedidoPendenteDeNotaFiscal(pedidoDeCompra);
                contador++;
            } catch (MessagingException e) {
                logger.error("Erro ao enviar email para pedido: {}", pedidoDeCompra.getNumeroDePedido(), e);
            }
        }
        logger.info("Envio de emails concluído. Total de emails enviados: {}", contador);
    }

    public void debitarValorDaNotaFiscalNoPedido(NotaFiscal nf) {
        logger.info("Iniciando débito de nota fiscal no pedido. Valor: {}", nf.getValorAPagar());
        Double saldoTotal = somarValoresDosPedidos(nf.getPedidoDeCompra());
        logger.debug("Saldo total dos pedidos: {}", saldoTotal);

        if (saldoTotal < nf.getValorAPagar()) {
            logger.error("Saldo insuficiente para débito. Saldo: {}, Valor nota: {}", saldoTotal, nf.getValorAPagar());
            throw new PedidoDeCompraSemSaldoException("Pedido não possui saldo sufuciente");
        }

        Double valorNota = nf.getValorAPagar();
        for (PedidoDeCompra pedidoDeCompra : nf.getPedidoDeCompra()) {
            valorNota = atualizarPedido(pedidoDeCompra, valorNota);
        }
        logger.info("Débito de nota fiscal concluído com sucesso");
    }

    private Double somarValoresDosPedidos(List<PedidoDeCompra> pedidoDeCompras) {
        Double soma = 0.0;

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            soma += pedidoDeCompra.getSaldo();
        }

        logger.debug("Soma dos valores dos pedidos: {}", soma);
        return soma;
    }

    private Double atualizarPedido(PedidoDeCompra pedidoDeCompra, Double valor) {
        logger.debug("Atualizando pedido: {}. Valor a debitar: {}", pedidoDeCompra.getNumeroDePedido(), valor);
        PedidoDeCompra pedidoBancoDeDados = procurarPedidoDeCompraPeloNumeroDePedido(pedidoDeCompra.getNumeroDePedido());
        Double saldoAtual = pedidoBancoDeDados.getSaldo();

        if (saldoAtual >= valor) {
            pedidoBancoDeDados.setSaldo(saldoAtual - valor);
            pedidoDeCompraRespository.save(pedidoBancoDeDados);
            logger.debug("Pedido {} atualizado. Novo saldo: {}", pedidoDeCompra.getNumeroDePedido(), pedidoBancoDeDados.getSaldo());
            return 0.0;
        } else {
            Double valorRestante = valor - saldoAtual;
            pedidoBancoDeDados.setSaldo(0.0);
            pedidoDeCompraRespository.save(pedidoBancoDeDados);
            logger.debug("Pedido {} zerado. Valor restante a processar: {}", pedidoDeCompra.getNumeroDePedido(), valorRestante);
            return valorRestante;
        }
    }
}
