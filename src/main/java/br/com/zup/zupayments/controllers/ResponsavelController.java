package br.com.zup.zupayments.controllers;

import br.com.zup.zupayments.core.ampper.CadastrarResponsavelDTOToResponsavelMapper;
import br.com.zup.zupayments.dtos.responsavel.entrada.CadastrarResponsavelDTO;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.services.ResponsavelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("responsaveis/")
@Tag(name = "API REST de responsaveis")
public class ResponsavelController {
    private static final Logger log = LoggerFactory.getLogger(ResponsavelController.class);
    private final ResponsavelService responsavelService;
    private final CadastrarResponsavelDTOToResponsavelMapper mapper;

    public ResponsavelController(ResponsavelService responsavelService,
                                 CadastrarResponsavelDTOToResponsavelMapper mapper) {
        this.responsavelService = responsavelService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "cadastrar um novo responsavel")
    public Responsavel cadastrarResponsavel(@RequestBody @Valid
                                                CadastrarResponsavelDTO cadastrarResponsavelDTO) {
        log.info("Iniciando cadastro de responsável através do controller");
        Responsavel responsavel = mapper.map(cadastrarResponsavelDTO);
        return responsavelService.cadastrarResponsavel(responsavel);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativa um responsavel pelo email")
    public void ativarOuDesativarResponsavel(@RequestParam(name = "email") String emailResponsavel) {
        log.info("Iniciando ativação/desativação de responsável com email: {}", emailResponsavel);
        responsavelService.ativarOuDesativarResponsavel(emailResponsavel);
    }
}
