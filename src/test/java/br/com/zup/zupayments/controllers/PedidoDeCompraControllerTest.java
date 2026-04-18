package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.pedidodecompras.saida.SaidaCadastroPedidoDeCompraDTO;
import br.com.zup.zupayments.enums.FormaDePagamento;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.models.PedidoDeCompra;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.services.FornecedorService;
import br.com.zup.zupayments.services.PedidoDeCompraService;
import br.com.zup.zupayments.services.ResponsavelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebMvcTest(PedidoDeCompraController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoDeCompraControllerTest {

    @MockitoBean
    private PedidoDeCompraService pedidoDeCompraService;

    @MockitoBean
    private FornecedorService fornecedorService;

    @MockitoBean
    private ResponsavelService responsavelService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private PedidoDeCompra pedidoDeCompra;
    private Responsavel responsavel;
    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Preparar responsável
        responsavel = new Responsavel();
        responsavel.setEmail("rodrigo@email.com");
        responsavel.setNomeCompleto("Rodrigo Vaz");
        responsavel.setNomeDoProjeto("FACILITIES");
        responsavel.setAtivo(true);

        // Preparar fornecedor
        fornecedor = new Fornecedor();
        fornecedor.setCnpjOuCpf("084.215.150-80");
        fornecedor.setRazaoSocial("Fornecedor XYZ");

        // Preparar pedido de compra
        pedidoDeCompra = new PedidoDeCompra();
        pedidoDeCompra.setNumeroDePedido(UUID.randomUUID());
        pedidoDeCompra.setDataDeVencimento(LocalDate.parse("2021-06-01"));
        pedidoDeCompra.setDataDePagamento(LocalDate.parse("2021-05-05"));
        pedidoDeCompra.setSaldo(200.00);
        pedidoDeCompra.setDataLimiteEnvio(LocalDate.parse("2021-06-01"));
        pedidoDeCompra.setFormaDePagamento(FormaDePagamento.BOLETO);
        pedidoDeCompra.setResponsavel(responsavel);
        pedidoDeCompra.setFornecedor(fornecedor);
    }

    @Test
    void testarCadastroPedidoDeCompraComSucesso() throws Exception {
        // Arrange
        SaidaCadastroPedidoDeCompraDTO saidaDTO = SaidaCadastroPedidoDeCompraDTO.converterModeloParaDto(pedidoDeCompra);
        String respostaEsperada = objectMapper.writeValueAsString(saidaDTO);
        String requisicaoJson = "{\"dataDeVencimento\":\"01/06/2021\",\"valorAproximado\":200.0," +
                "\"dataDePagamento\":\"05/05/2021\",\"emailResponsavel\":\"rodrigo@email.com\"," +
                "\"dataLimiteEnvio\":\"01/06/2021\",\"formaDePagamento\":\"BOLETO\"," +
                "\"cnpjOuCpf\":\"084.215.150-80\",\"saldo\":200.00}";

        Mockito.when(pedidoDeCompraService.cadastrarNovoPedidoDeCompra(Mockito.any(PedidoDeCompra.class)))
                .thenReturn(pedidoDeCompra);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.post("/pedidos/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(respostaEsperada));

        // Verify
        Mockito.verify(pedidoDeCompraService, Mockito.times(1)).cadastrarNovoPedidoDeCompra(Mockito.any(PedidoDeCompra.class));
    }

    @Test
    void testarObterTodosPedidosDeCompraComResponsavelAtivo() throws Exception {
        // Arrange
        List<PedidoDeCompra> listaPedidos = criarListaPedidos(2);

        Mockito.when(pedidoDeCompraService.obterTodosPedidosDeCompraComResponsavelAtivo(Mockito.eq(true)))
                .thenReturn(listaPedidos);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pedidos/responsaveis?ativo=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].responsavel.email").value("responsavel0@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fornecedor.cnpjOuCpf").value("084.215.150-80"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].responsavel.email").value("responsavel1@email.com"));

        // Verify
        Mockito.verify(pedidoDeCompraService, Mockito.times(1)).obterTodosPedidosDeCompraComResponsavelAtivo(Mockito.eq(true));
    }

    @Test
    void testarObterTodosPedidosDeCompraComResponsavelInativo() throws Exception {
        // Arrange
        List<PedidoDeCompra> listaPedidos = new ArrayList<>();

        Mockito.when(pedidoDeCompraService.obterTodosPedidosDeCompraComResponsavelAtivo(Mockito.eq(false)))
                .thenReturn(listaPedidos);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pedidos/responsaveis?ativo=false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        // Verify
        Mockito.verify(pedidoDeCompraService, Mockito.times(1)).obterTodosPedidosDeCompraComResponsavelAtivo(Mockito.eq(false));
    }

    @Test
    void testarObterPedidosComNotaFiscaisPendentesDeEnvio() throws Exception {
        // Arrange
        List<PedidoDeCompra> listaPedidos = criarListaPedidos(2);

        Mockito.when(pedidoDeCompraService.obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(listaPedidos);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pedidos/pendentes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].formaDePagamento").value("BOLETO"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].saldo").value(201.0));

        // Verify
        Mockito.verify(pedidoDeCompraService, Mockito.times(1))
                .obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(
                        Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testarObterTodosPedidosDeCompra() throws Exception {
        // Arrange
        List<PedidoDeCompra> listaPedidos = criarListaPedidos(3);

        Mockito.when(pedidoDeCompraService.obterTodosOsPedidoDeCompra())
                .thenReturn(listaPedidos);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pedidos/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].numeroDePedido").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].formaDePagamento").value("BOLETO"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].responsavel.email").value("responsavel0@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].saldo").value(201.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].saldo").value(202.0));

        // Verify
        Mockito.verify(pedidoDeCompraService, Mockito.times(1)).obterTodosOsPedidoDeCompra();
    }

    /**
     * Cria uma lista de pedidos de compra com dados padrão para testes
     * @param quantidade quantidade de pedidos a criar
     * @return lista de pedidos de compra
     */
    private List<PedidoDeCompra> criarListaPedidos(int quantidade) {
        List<PedidoDeCompra> listaPedidos = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            PedidoDeCompra pedido = criarNovoPedido(i);
            listaPedidos.add(pedido);
        }

        return listaPedidos;
    }

    /**
     * Cria um novo pedido de compra com dados padrão para testes
     * @param indice índice para diferenciar pedidos
     * @return pedido de compra configurado
     */
    private PedidoDeCompra criarNovoPedido(int indice) {
        Responsavel responsavelTest = new Responsavel();
        responsavelTest.setEmail("responsavel" + indice + "@email.com");
        responsavelTest.setNomeCompleto("Responsável " + indice);
        responsavelTest.setNomeDoProjeto("PROJETO_" + indice);
        responsavelTest.setAtivo(true);

        Fornecedor fornecedorTest = new Fornecedor();
        fornecedorTest.setCnpjOuCpf("084.215.150-80");
        fornecedorTest.setRazaoSocial("Fornecedor " + indice);

        PedidoDeCompra pedido = new PedidoDeCompra();
        pedido.setNumeroDePedido(UUID.randomUUID());
        pedido.setDataDePagamento(LocalDate.parse("2021-05-05"));
        pedido.setDataDeVencimento(LocalDate.parse("2021-06-01"));
        pedido.setSaldo(200.00 + indice);
        pedido.setDataLimiteEnvio(LocalDate.parse("2021-06-01"));
        pedido.setFormaDePagamento(FormaDePagamento.BOLETO);
        pedido.setCancelado(false);
        pedido.setResponsavel(responsavelTest);
        pedido.setFornecedor(fornecedorTest);

        return pedido;
    }
}