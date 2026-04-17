package br.com.zup.zupayments.services;

import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.NotaFiscalRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ContextConfiguration(classes = NotaFiscalService.class)
class NotaFiscalServiceTest {

    @Autowired
    private NotaFiscalService notaFiscalService;

    @MockitoBean
    private NotaFiscalRepository notaFiscalRepository;

    @MockitoBean
    private FornecedorService fornecedorService;

    @MockitoBean
    private ResponsavelService responsavelService;

    @MockitoBean
    private PedidoDeCompraService pedidoDeCompraService;

    private NotaFiscal notaFiscalteste;
    private Responsavel responsavel;
    private Fornecedor fornecedor;
    private PedidoDeCompra pedidoDeCompra;

    @BeforeEach
    void setup(){

        this.notaFiscalteste = new NotaFiscal();

        this.responsavel  = new Responsavel();
        this.responsavel.setAtivo(true);
        this.responsavel.setEmail("bomdia@zup.com.br");
        this.responsavel.setNomeCompleto("bom dia silva");

        this.fornecedor = new Fornecedor();
        fornecedor.setRazaoSocial("Uma Empresa Ltda");
        fornecedor.setAtivo(true);
        fornecedor.setCategoriaDeCusto(null);
        fornecedor.setEmail("ceo@umaempresa.com");
        fornecedor.setCnpjOuCpf("12.234.543/8765-56");
        fornecedor.setLogradouro("Rua da empresa");
        fornecedor.setNumero("272");
        fornecedor.setCep("12122976");
        fornecedor.setBairro("Bairro da empresa");
        fornecedor.setEstado("Estado da empresa");

        this.pedidoDeCompra = new PedidoDeCompra();
        this.pedidoDeCompra.setNumeroDePedido(UUID.randomUUID());

        this.notaFiscalteste.setFornecedor(fornecedor);
        this.notaFiscalteste.setValorAPagar(300.50);
        this.notaFiscalteste.setDataDeEmissao(LocalDate.now());
        this.notaFiscalteste.setNumeroDaNota(4L);
        this.notaFiscalteste.setDataDeEnvio(LocalDate.now());
        this.notaFiscalteste.setResponsavel(this.responsavel);
        this.notaFiscalteste.setPedidoDeCompra(Arrays.asList(this.pedidoDeCompra));
        this.notaFiscalteste.setCancelar(false);
    }

    @Test
    void testarCadastroDeNotaFiscalOk() {
        Mockito.when(notaFiscalRepository.save(Mockito.any(NotaFiscal.class))).thenReturn(notaFiscalteste);
        Mockito.when(fornecedorService.pesquisarFornecedorPorCnpjOuCpf(Mockito.anyString())).thenReturn(fornecedor);
        Mockito.when(pedidoDeCompraService.procurarPedidoDeCompraPeloNumeroDePedido(Mockito.any())).thenReturn(pedidoDeCompra);
        Mockito.when(responsavelService.procurarResponsavelPorEmail(Mockito.anyString())).thenReturn(responsavel);

        NotaFiscal testNota = notaFiscalService.cadastrarNotaFiscal(notaFiscalteste);
        Assertions.assertEquals(testNota, notaFiscalteste);
    }

    @Test
    void testarAtivarOuDesativarNotaFiscal() {
        notaFiscalteste.setId(UUID.randomUUID());
        Optional<NotaFiscal> optionalNotaFiscal = Optional.of(notaFiscalteste);
        Mockito.when(notaFiscalRepository.findById(Mockito.any())).thenReturn(optionalNotaFiscal);
        Mockito.when(notaFiscalRepository.save(Mockito.any())).thenReturn(notaFiscalteste);
        NotaFiscal teste = notaFiscalService.cancelarNF(UUID.randomUUID());
        Assertions.assertEquals(teste, notaFiscalteste);
    }

    @Test
    void testarAtivarOuDesativarNotaFiscalError() {
        Optional<NotaFiscal> optionalNotaFiscal = Optional.empty();
        Mockito.when(notaFiscalRepository.findById(Mockito.any())).thenReturn(optionalNotaFiscal);
        Assertions.assertThrows(RuntimeException.class, () -> {
            notaFiscalService.cancelarNF(UUID.randomUUID());
        });
    }
}
