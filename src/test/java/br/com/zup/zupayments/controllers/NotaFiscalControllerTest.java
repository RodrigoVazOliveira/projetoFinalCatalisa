package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.notafiscal.entrada.CadastrarNotaFiscalDTO;
import br.com.zup.zupayments.models.NotaFiscal;
import br.com.zup.zupayments.services.NotaFiscalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.UUID;

/**
 * Testes unitários para o NotaFiscalController
 *
 * @WebMvcTest realiza teste isolado apenas do controller
 * Não carrega contexto completo da aplicação, apenas MVC
 */
@WebMvcTest(NotaFiscalController.class)
class NotaFiscalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotaFiscalService notaFiscalService;

    private NotaFiscal notaFiscalteste;
    private CadastrarNotaFiscalDTO cadastrarNotaFiscalDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // Record constructor para CadastrarNotaFiscalDTO
        this.cadastrarNotaFiscalDTO = new CadastrarNotaFiscalDTO(
                1L, // numeroDaNota
                "05.792.077/0001-65", // cnpjOuCpfFornecedor
                2000.00, // valorAPagar
                LocalDate.now(), // dataDeEmissao
                Arrays.asList(UUID.randomUUID(), UUID.randomUUID()), // pedidoDeCompras
                LocalDate.now(), // dataDeEnvio
                "rodrigo.vaz@zup.com.br" // emailDoResponsavel
        );

        this.notaFiscalteste = this.cadastrarNotaFiscalDTO.converterDtoParaModelo();
    }

    @Test
    void testarCadastrarNotaFiscal() throws Exception {
        this.notaFiscalteste.setId(UUID.randomUUID());
        String jsonEntrada = objectMapper.writeValueAsString(this.cadastrarNotaFiscalDTO);

        Mockito.when(notaFiscalService.cadastrarNotaFiscal(Mockito.any())).thenReturn(notaFiscalteste);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/notas_fiscais/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonEntrada)
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testarCancelamentoDeNotaFiscal() throws Exception {
        UUID id = UUID.randomUUID();
        this.notaFiscalteste.setId(id);

        Mockito.when(notaFiscalService.cancelarNF(Mockito.any())).thenReturn(this.notaFiscalteste);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/notas_fiscais/" + id +
                                "/")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
