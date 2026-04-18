package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.dtos.fornecedor.entrada.CadastroDeFornecedorDTO;
import br.com.zup.zupayments.enums.CategoriaDeCusto;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.services.FornecedorService;
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

/**
 * Testes unitários para o FornecedorController
 *
 * @WebMvcTest realiza teste isolado apenas do controller
 * Não carrega contexto completo da aplicação, apenas MVC
 */
@WebMvcTest(FornecedorController.class)
class FornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FornecedorService fornecedorService;

    private Fornecedor fornecedor;
    private CadastroDeFornecedorDTO cadastroDeFornecedorDTO;

    @BeforeEach
    public void setup() {
        // ... existing code ...
        this.fornecedor = new Fornecedor();
        this.fornecedor.setCnpjOuCpf("23.524.377/0001-45");
        this.fornecedor.setRazaoSocial("Empresa 1");
        this.fornecedor.setLogradouro("Rua Dos bobos");
        this.fornecedor.setNumero("0");
        this.fornecedor.setBairro("Sem teto");
        this.fornecedor.setCidade("Sem Fim");
        this.fornecedor.setEstado("Sem Estado");
        this.fornecedor.setCep("2434424232");
        this.fornecedor.setTelefone("(99) 9999-9999");
        this.fornecedor.setEmail("rsdfasfdsdf@sfsd.com");
        this.fornecedor.setCategoriaDeCusto(CategoriaDeCusto.FACILITIES);
        this.fornecedor.setAtivo(true);

        this.cadastroDeFornecedorDTO = new CadastroDeFornecedorDTO(
                null, // cpf
                "23.524.377/0001-45", // cnpj
                "Empresa 1", // razaoSocial
                "Rua Dos bobos", // logradouro
                "0", // numero
                "Sem teto", // bairro
                "Sem Fim", // cidade
                "Sem Estado", // estado
                "2434424232", // cep
                "(99) 9999-9999", // telefone
                "rsdfasfdsdf@sfsd.com", // email
                CategoriaDeCusto.FACILITIES // categoriaDeCusto
        );
    }

    @Test
    void testarCadastroDeFornecedor() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String fornecedorJson = objectMapper.writeValueAsString(cadastroDeFornecedorDTO);
        String respostaJson = objectMapper.writeValueAsString(fornecedor);

        Mockito.when(fornecedorService.cadastrarFornecedor(Mockito.any())).thenReturn(fornecedor);

        mockMvc.perform(MockMvcRequestBuilders.post("/fornecedores/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fornecedorJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(respostaJson));

    }

    @Test
    void ativarOuDesativarFornecedor() throws Exception {
        String url = "/fornecedores/?cnpjoucpf=23.524.377/0001-45";
        Mockito.doNothing().when(fornecedorService).ativarOuDesativarFornecedor(Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders.patch(url))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
