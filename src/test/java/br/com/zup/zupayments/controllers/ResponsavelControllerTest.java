package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.responsavel.entrada.CadastrarResponsavelDTO;
import br.com.zup.zupayments.models.Responsavel;
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

@WebMvcTest(ResponsavelController.class)
class ResponsavelControllerTest {

    @MockitoBean
    private ResponsavelService responsavelService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testarCadastrarResponsavelOk() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CadastrarResponsavelDTO cadastrarResponsavelDTO = new CadastrarResponsavelDTO(
                "emaisdfaadsl@email.com",
                "Zup da Silva",
                "Zupper"
        );

        Responsavel responsavel = new Responsavel();
        responsavel.setAtivo(true);
        responsavel.setEmail("email@email.com");
        responsavel.setNomeCompleto("Zup da Silva");
        responsavel.setNomeDoProjeto("Zupper");

        String entradaJson = objectMapper.writeValueAsString(cadastrarResponsavelDTO);
        String saidaJson   = objectMapper.writeValueAsString(responsavel);

        Mockito.when(responsavelService.cadastrarResponsavel(Mockito.any())).thenReturn(responsavel);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/responsaveis/")
                .contentType(MediaType.APPLICATION_JSON).content(entradaJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(saidaJson));
    }

    @Test
    void ativarOuDesativarResponsavel() throws Exception {
        String url = "/responsaveis/?email=rodrigo.vaz@zup.com.br";
        Mockito.doNothing().when(responsavelService).ativarOuDesativarResponsavel(Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders.patch(url)).andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}

