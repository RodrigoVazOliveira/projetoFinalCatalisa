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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(PedidoDeCompraController.class)
class PedidoDeCompraControllerTest {

    @MockitoBean
    private PedidoDeCompraService pedidoDeCompraService;

    @MockitoBean
    private FornecedorService fornecedorService;

    @MockitoBean
    private ResponsavelService responsavelService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private List<PedidoDeCompra> createListPedidoCompra() {
        List<PedidoDeCompra> pedidoDeCompras = new ArrayList<>();

        for (Long i = 0L; i < 2; i++) {
            pedidoDeCompras.add(criarNovoPedido(i));
        }

        return  pedidoDeCompras;
    }

    private PedidoDeCompra criarNovoPedido(Long numeroDePedido) {
        PedidoDeCompra pedido = new PedidoDeCompra();
        pedido.setNumeroDePedido(numeroDePedido);
        pedido.setDataDePagamento(LocalDate.parse("2021-05-01"));
        pedido.setSaldo(2.000);
        pedido.setDataDePagamento(LocalDate.parse("2021-05-01"));
        pedido.setDataLimiteEnvio(LocalDate.parse("2021-05-01"));
        pedido.setFormaDePagamento(FormaDePagamento.BOLETO);
        pedido.setDataDeVencimento(LocalDate.parse("2021-05-01"));

        Responsavel responsavelTestLista = new Responsavel();
        responsavelTestLista.setEmail("email@email.com");
        responsavelTestLista.setNomeCompleto("Rodrigo Vaz");
        responsavelTestLista.setNomeDoProjeto("FACILITIES");
        pedido.setResponsavel(responsavelTestLista);

        Fornecedor fornecedorTest = new Fornecedor();
        fornecedorTest.setCnpjOuCpf("084.215.150-80");
        pedido.setFornecedor(fornecedorTest);

        return pedido;
    }

    @Test
    void testarCadastroPedidoDeCompra() throws Exception{
        PedidoDeCompra pedidoDeCompra = criarNovoPedido(1L);
        SaidaCadastroPedidoDeCompraDTO saidaCadastroPedidoDeCompraDTO = SaidaCadastroPedidoDeCompraDTO.converterModeloParaDto(pedidoDeCompra);
        String response = objectMapper.writeValueAsString(saidaCadastroPedidoDeCompraDTO);
        String cadastroJson = "{\"dataDeVencimento\":\"01/06/2021\",\"valorAproximado\":2.0,\"dataDePagamento\":\"05/05/2021\",\"emailResponsavel\":\"email@email.com\",\"dataLimiteEnvio\":\"01/06/2021\",\"formaDePagamento\":\"BOLETO\",\"cnpjOuCpf\":\"084.215.150-80\",\"saldo\": 200.00}";
        Mockito.when(pedidoDeCompraService.cadastrarNovoPedidoDeCompra(Mockito.any())).thenReturn(pedidoDeCompra);

        mockMvc.perform(MockMvcRequestBuilders.post("/pedidos/").
                contentType(MediaType.APPLICATION_JSON).content(cadastroJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    void testarObterTodosPedidosDeCompraComResponsavelInativo() throws Exception {
        List<PedidoDeCompra> listPedidoCompra = createListPedidoCompra();
        String jsonResposta = objectMapper.writeValueAsString(listPedidoCompra);
        Mockito.when(pedidoDeCompraService.obterTodosPedidosDeCompraComResponsavelAtivo(Mockito.anyBoolean())).thenReturn(listPedidoCompra);


        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/responsaveis?ativo=true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonResposta));
    }

    @Test
    void testarObterPedidosComNotaFiscaisPendentesDeEnvio() throws Exception{
        List<PedidoDeCompra> listPedidoCompra = createListPedidoCompra();

        String retornoJson = objectMapper.writeValueAsString(listPedidoCompra);

        Mockito.when(pedidoDeCompraService.obterTodosPedidosDeCompraComValorMaiorQueZeroEResponsaveisAtivo(Mockito.anyDouble(), Mockito.anyBoolean(), Mockito.any())).thenReturn(listPedidoCompra);
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/pendentes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(retornoJson));
    }
}