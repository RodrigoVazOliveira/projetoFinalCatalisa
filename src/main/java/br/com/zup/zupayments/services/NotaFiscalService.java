package br.com.zup.zupayments.services;

import br.com.zup.zupayments.exceptions.erros.NotaFiscalNaoCadastradaException;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.repositories.NotaFiscalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotaFiscalService {
    private static final Logger log = LoggerFactory.getLogger(NotaFiscalService.class);
    private final NotaFiscalRepository notaFiscalRepository;
    private final ResponsavelService responsavelService;
    private final FornecedorService fornecedorService;
    private final PedidoDeCompraService pedidoDeCompraService;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository,
                             ResponsavelService responsavelService,
                             FornecedorService fornecedorService,
                             PedidoDeCompraService pedidoDeCompraService) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.responsavelService = responsavelService;
        this.fornecedorService = fornecedorService;
        this.pedidoDeCompraService = pedidoDeCompraService;
    }

    public NotaFiscal cadastrarNotaFiscal(NotaFiscal fiscal) {
        log.info("Iniciando cadastro de nota fiscal. Número: {}, Valor: R$ {}",
                fiscal.getNumeroDaNota(), fiscal.getValorAPagar());

        log.debug("Buscando responsável com email: {}", fiscal.getResponsavel().getEmail());
        fiscal.setResponsavel(responsavelService.procurarResponsavelPorEmail(fiscal.getResponsavel().getEmail()));

        log.debug("Buscando fornecedor com CNPJ/CPF: {}", fiscal.getFornecedor().getCnpjOuCpf());
        fiscal.setFornecedor(fornecedorService.pesquisarFornecedorPorCnpjOuCpf(fiscal.getFornecedor().getCnpjOuCpf()));

        log.debug("Gerando lista de pedidos de compra");
        fiscal.setPedidoDeCompra(gerarListaDePedidoDeCompraParaCadastrar(fiscal.getPedidoDeCompra()));

        log.debug("Salvando nota fiscal no banco de dados");
        NotaFiscal novaNotaFiscal = notaFiscalRepository.save(fiscal);

        log.debug("Debitando valor da nota fiscal nos pedidos de compra");
        pedidoDeCompraService.debitarValorDaNotaFiscalNoPedido(novaNotaFiscal);

        log.info("Nota fiscal cadastrada com sucesso. ID: {}, Número: {}, Responsável: {}",
                novaNotaFiscal.getId(), novaNotaFiscal.getNumeroDaNota(),
                novaNotaFiscal.getResponsavel().getEmail());

        return novaNotaFiscal;
    }

    private List<PedidoDeCompra> gerarListaDePedidoDeCompraParaCadastrar(List<PedidoDeCompra> pedidoDeCompras) {
        log.debug("Iniciando geração de lista de pedidos de compra. Total de pedidos: {}", pedidoDeCompras.size());

        List<PedidoDeCompra> listaDePedidoDeCompraParaCadastrar = new ArrayList<>();

        for (PedidoDeCompra pedidoDeCompra : pedidoDeCompras) {
            log.debug("Buscando pedido de compra com número: {}", pedidoDeCompra.getNumeroDePedido());
            PedidoDeCompra pedidoBuscado = pedidoDeCompraService.procurarPedidoDeCompraPeloNumeroDePedido(
                    pedidoDeCompra.getNumeroDePedido()
            );
            listaDePedidoDeCompraParaCadastrar.add(pedidoBuscado);
            log.debug("Pedido de compra {} adicionado à lista", pedidoBuscado.getNumeroDePedido());
        }

        log.debug("Lista de pedidos de compra gerada com sucesso. Total: {}",
                listaDePedidoDeCompraParaCadastrar.size());

        return listaDePedidoDeCompraParaCadastrar;
    }

    public NotaFiscal pesquisarNotaFiscalPeloId(UUID id) {
        log.info("Buscando nota fiscal com ID: {}", id);

        Optional<NotaFiscal> optionalNotaFiscal = notaFiscalRepository.findById(id);

        if (optionalNotaFiscal.isEmpty()) {
            log.warn("Nota fiscal não encontrada. ID: {}", id);
            throw new NotaFiscalNaoCadastradaException("Nota fiscal não esta cadastrada");
        }

        log.info("Nota fiscal encontrada com sucesso. ID: {}, Número: {}",
                id, optionalNotaFiscal.get().getNumeroDaNota());

        return optionalNotaFiscal.get();
    }

    public NotaFiscal cancelarNF(UUID id) {
        log.info("Iniciando cancelamento de nota fiscal. ID: {}", id);

        NotaFiscal notaFiscalAtual = pesquisarNotaFiscalPeloId(id);

        Boolean statusAnterior = notaFiscalAtual.getCancelar();
        notaFiscalAtual.setCancelar(!notaFiscalAtual.getCancelar());

        NotaFiscal notaFiscalAtualizada = notaFiscalRepository.save(notaFiscalAtual);

        log.info("Nota fiscal cancelada com sucesso. ID: {}, Número: {}, Status anterior: {}, Status novo: {}",
                id, notaFiscalAtualizada.getNumeroDaNota(), statusAnterior, notaFiscalAtualizada.getCancelar());

        return notaFiscalAtualizada;
    }

    public Iterable<NotaFiscal> obterTodasNotaFiscalComIntervaloDeDataDeEmissao(LocalDate dataInicial, LocalDate dataFinal) {
        log.info("Buscando notas fiscais com intervalo de data de emissão. De: {} até: {}",
                dataInicial, dataFinal);

        Iterable<NotaFiscal> notasFiscais = notaFiscalRepository.findAllByDataDeEmissaoBetween(dataInicial, dataFinal);

        long totalNotas = notasFiscais.spliterator().getExactSizeIfKnown();
        log.info("Busca de notas fiscais por intervalo concluída. Total encontrado: {}", totalNotas);

        return notasFiscais;
    }
}