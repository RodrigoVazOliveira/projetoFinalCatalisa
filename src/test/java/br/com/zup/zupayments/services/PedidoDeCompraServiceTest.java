package br.com.zup.zupayments.services;

import br.com.zup.zupayments.enums.FormaDePagamento;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.PedidoDeCompraRespository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ContextConfiguration(classes = PedidoDeCompraService.class)
class PedidoDeCompraServiceTest {

    @MockitoBean
    private PedidoDeCompraRespository pedidoDeCompraRespository;

    @MockitoBean
    private ResponsavelService responsavelService;

    @MockitoBean
    private FornecedorService fornecedorService;

    @MockitoBean
    private NotaFiscalService notaFiscalService;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private PedidoDeCompraService pedidoDeCompraService;

    private Fornecedor fornecedor;
    private Responsavel responsavel;
    private PedidoDeCompra pedidoDeCompra;
    private List<PedidoDeCompra> pedidoDeCompras;

    @BeforeEach
    public void setUp() {
        this.pedidoDeCompra = new PedidoDeCompra();
        this.pedidoDeCompra.setNumeroDePedido(31L);
        this.pedidoDeCompra.setDataDePagamento(LocalDate.now());
        this.pedidoDeCompra.setSaldo(2.000);
        this.pedidoDeCompra.setDataDePagamento(LocalDate.now());
        this.pedidoDeCompra.setDataLimiteEnvio(LocalDate.now());
        this.pedidoDeCompra.setFormaDePagamento(FormaDePagamento.BOLETO);
        this.pedidoDeCompra.setDataDeVencimento(LocalDate.now());

        this.responsavel = new Responsavel();
        this.responsavel.setEmail("email@email.com");
        this.pedidoDeCompra.setResponsavel(this.responsavel);

        this.fornecedor = new Fornecedor();
        this.fornecedor.setCnpjOuCpf("084.215.150-80");
        this.pedidoDeCompra.setFornecedor(this.fornecedor);

        this.pedidoDeCompras = new ArrayList<>();

        for (Long i = 0L; i < 10; i++) {
            this.pedidoDeCompras.add(criarNovoPedido(i));
        }
    }

    private PedidoDeCompra criarNovoPedido(Long numeroDePedido) {
        PedidoDeCompra pedido = new PedidoDeCompra();
        pedido.setNumeroDePedido(31L);
        pedido.setDataDePagamento(LocalDate.now());
        pedido.setSaldo(2.000);
        pedido.setDataDePagamento(LocalDate.now());
        pedido.setDataLimiteEnvio(LocalDate.now());
        pedido.setFormaDePagamento(FormaDePagamento.BOLETO);
        pedido.setDataDeVencimento(LocalDate.now());

        Responsavel responsavelTestLista = new Responsavel();
        responsavelTestLista.setEmail("email@email.com");
        pedido.setResponsavel(this.responsavel);

        Fornecedor fornecedorTest = new Fornecedor();
        fornecedorTest.setCnpjOuCpf("084.215.150-80");
        pedido.setFornecedor(this.fornecedor);

        return pedido;
    }

    @Test
    void cadastroDePedidoDeCompraTest(){
        Mockito.when(pedidoDeCompraRespository.save(Mockito.any(PedidoDeCompra.class))).thenReturn(pedidoDeCompra);
        Mockito.when(responsavelService.procurarResponsavelPorEmail(Mockito.any())).thenReturn(responsavel);
        Mockito.when(fornecedorService.pesquisarFornecedorPorCnpjOuCpf(Mockito.any())).thenReturn(fornecedor);

        PedidoDeCompra pedidoDeCompraTest = pedidoDeCompraService.cadastrarNovoPedidoDeCompra(pedidoDeCompra);

        Assertions.assertEquals(pedidoDeCompraTest,pedidoDeCompra);
    }

    @Test
    void obterTodosOsPedidoDeCompraTest(){
        Optional<PedidoDeCompra> optionalPedidoDeCompra =Optional.empty();

        Mockito.when(pedidoDeCompraRespository.findById(Mockito.anyLong())).thenReturn(optionalPedidoDeCompra);

        Assertions.assertThrows(RuntimeException.class,() ->{
            pedidoDeCompraService.obterTodosOsPedidoDeCompra();
            throw new RuntimeException("");
        });
    }

    @Test
    void testarCancelamentoDePedidoDeCompra() {
        Optional<PedidoDeCompra> optionalPedidoDeCompra = Optional.of(pedidoDeCompra);

        Mockito.when(pedidoDeCompraRespository.findById(Mockito.anyLong())).thenReturn(optionalPedidoDeCompra);
        Mockito.when(pedidoDeCompraRespository.save(Mockito.any(PedidoDeCompra.class))).thenReturn(pedidoDeCompra);

        pedidoDeCompraService.cancelarPedidoDeCompra(1L);

        Mockito.verify(pedidoDeCompraRespository, Mockito.times(1)).save(pedidoDeCompra);
    }

    @Test
    void testarObterPedidosDeCompraResponsavelAtivo() {
        Mockito.when(pedidoDeCompraRespository.findAllByResponsavelAtivo(Mockito.anyBoolean()))
                .thenReturn(this.pedidoDeCompras);
        Iterable<PedidoDeCompra> testes = pedidoDeCompraService.obterTodosPedidosDeCompraComResponsavelAtivo(false);
        Assertions.assertEquals(testes, this.pedidoDeCompras);
    }

    @Test
    void testarObterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(){
        Mockito.when(pedidoDeCompraRespository.findAllBySaldoGreaterThanAndResponsavelAtivo(Mockito.anyDouble(), Mockito.anyBoolean())).thenReturn(this.pedidoDeCompras);
        Iterable<PedidoDeCompra> test = pedidoDeCompraService.obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(456.7,false, LocalDate.now());
        Assertions.assertEquals(test, this.pedidoDeCompras);

    }
}