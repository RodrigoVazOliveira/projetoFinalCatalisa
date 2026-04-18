package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.core.ampper.CadastrarResponsavelDTOToResponsavelMapper;
import br.com.zup.zupayments.dtos.responsavel.entrada.CadastrarResponsavelDTO;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.services.ResponsavelService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(ResponsavelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResponsavelControllerTest {

    @MockitoBean
    private ResponsavelService responsavelService;

    @MockitoBean
    private CadastrarResponsavelDTOToResponsavelMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private CadastrarResponsavelDTO cadastrarResponsavelDTO;
    private Responsavel responsavel;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Preparar dados de entrada
        cadastrarResponsavelDTO = new CadastrarResponsavelDTO(
                "rodrigo@email.com",
                "Rodrigo da Silva",
                "Projeto X"
        );

        // Preparar dados de saída
        responsavel = new Responsavel();
        responsavel.setAtivo(true);
        responsavel.setEmail("rodrigo@email.com");
        responsavel.setNomeCompleto("Rodrigo da Silva");
        responsavel.setNomeDoProjeto("Projeto X");
    }

    @Test
    void testarCadastrarResponsavelComSucesso() throws Exception {
        // Arrange
        Responsavel responsavelEsperado = new Responsavel();
        responsavelEsperado.setAtivo(true);
        responsavelEsperado.setEmail("rodrigo@email.com");
        responsavelEsperado.setNomeCompleto("Rodrigo da Silva");
        responsavelEsperado.setNomeDoProjeto("Projeto X");

        String entradaJson = objectMapper.writeValueAsString(cadastrarResponsavelDTO);
        String saidaJson = objectMapper.writeValueAsString(responsavelEsperado);

        // Configurar mocks
        Mockito.when(mapper.map(Mockito.any(CadastrarResponsavelDTO.class)))
                .thenReturn(responsavelEsperado);
        Mockito.when(responsavelService.cadastrarResponsavel(Mockito.any(Responsavel.class)))
                .thenReturn(responsavelEsperado);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.post("/responsaveis/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entradaJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(saidaJson));

        // Verify
        Mockito.verify(responsavelService, Mockito.times(1)).cadastrarResponsavel(Mockito.any(Responsavel.class));
    }


    @Test
    void testarAtivarOuDesativarResponsavelComSucesso() throws Exception {
        // Arrange
        String email = "rodrigo@zup.com.br";
        String url = "/responsaveis/?email=" + email;

        Mockito.doNothing().when(responsavelService).ativarOuDesativarResponsavel(Mockito.eq(email));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch(url))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verify
        Mockito.verify(responsavelService, Mockito.times(1)).ativarOuDesativarResponsavel(Mockito.eq(email));
    }

    @Test
    void testarCadastrarResponsavelComDadosInvalidos() throws Exception {
        // Arrange
        CadastrarResponsavelDTO dtoInvalido = new CadastrarResponsavelDTO(
                "", // email vazio
                "", // nome vazio
                "" // projeto vazio
        );

        String entradaJson = objectMapper.writeValueAsString(dtoInvalido);

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.post("/responsaveis/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entradaJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testarCadastrarResponsavelEmailDuplicado() throws Exception {
        // Arrange
        String email = "rodrigo@email.com";
        Responsavel responsavelEsperado = new Responsavel();
        responsavelEsperado.setAtivo(true);
        responsavelEsperado.setEmail(email);
        responsavelEsperado.setNomeCompleto("Rodrigo da Silva");
        responsavelEsperado.setNomeDoProjeto("Projeto X");

        String entradaJson = objectMapper.writeValueAsString(cadastrarResponsavelDTO);

        // Configurar mock para lançar exceção de email duplicado
        Mockito.when(mapper.map(Mockito.any(CadastrarResponsavelDTO.class)))
                .thenReturn(responsavelEsperado);
        Mockito.when(responsavelService.cadastrarResponsavel(Mockito.any(Responsavel.class)))
                .thenThrow(new br.com.zup.zupayments.exceptions.erros.ResponsavelJaCadastradoException("Email já cadastrado"));

        // Act & Assert
        mockMvc.perform(
                MockMvcRequestBuilders.post("/responsaveis/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entradaJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
