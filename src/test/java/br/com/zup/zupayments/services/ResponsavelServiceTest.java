package br.com.zup.zupayments.services;

import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.ResponsavelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

@SpringBootTest
@ContextConfiguration(classes = ResponsavelService.class)
class ResponsavelServiceTest {

    @Autowired
    private ResponsavelService responsavelService;

    @MockitoBean
    private ResponsavelRepository responsavelRepository;
    private Responsavel responsavel;

    @BeforeEach
    public void setup(){
        this.responsavel = new Responsavel();
        this.responsavel.setEmail("bomdia@zup.com");
        this.responsavel.setNomeCompleto("Bom dia Silva");
        this.responsavel.setNomeCompleto("Facilites");
        this.responsavel.setAtivo(true);
    }

    @Test
    void testarCadastroDeResponsavelOk() {
        Mockito.when(responsavelRepository.save(Mockito.any(Responsavel.class))).thenReturn(responsavel);
        Responsavel testResponsavel = responsavelService.cadastrarResponsavel(responsavel);
        Assertions.assertEquals(testResponsavel, responsavel);
    }

    @Test
    void testarPesquisarResponsavelPeloEmailOk(){
        Optional<Responsavel> optionalResponsavel = Optional.of(responsavel);
        Mockito.when(responsavelRepository.findById(Mockito.anyString())).thenReturn(optionalResponsavel);
        Responsavel testBusca = responsavelService.procurarResponsavelPorEmail("bomdia@zup.com");
        Assertions.assertEquals(testBusca,responsavel);
    }

    @Test
    void testarAtivarEDesativarResponsavelOk() {
        Optional<Responsavel> optionalResponsavel = Optional.of(responsavel);
        Mockito.when(responsavelRepository.findById(Mockito.anyString())).thenReturn(optionalResponsavel);
        Mockito.when(responsavelRepository.save(Mockito.any(Responsavel.class))).thenReturn(responsavel);
        responsavelService.ativarOuDesativarResponsavel("responsavel@zup.com.br");
        Mockito.verify(responsavelRepository, Mockito.times(1)).save(Mockito.any(Responsavel.class));
    }
}


