package br.com.zup.zupayments.core.ampper;

import br.com.zup.zupayments.dtos.responsavel.entrada.CadastrarResponsavelDTO;
import br.com.zup.zupayments.models.Responsavel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CadastrarResponsavelDTOToResponsavelMapper implements Mapper<CadastrarResponsavelDTO, Responsavel> {
    private static final Logger log = LoggerFactory.getLogger(CadastrarResponsavelDTOToResponsavelMapper.class);

    @Override
    public Responsavel map(CadastrarResponsavelDTO cadastrarResponsavelDTO) {
        log.info("Iniciando mapeamento de CadastrarResponsavelDTO para Responsavel. Email: {}", cadastrarResponsavelDTO.email());

        Responsavel responsavel = new Responsavel();
        responsavel.setEmail(cadastrarResponsavelDTO.email());
        responsavel.setNomeCompleto(cadastrarResponsavelDTO.nomeCompleto());
        responsavel.setNomeDoProjeto(cadastrarResponsavelDTO.nomeDoProjeto());
        responsavel.setAtivo(true);

        log.info("Mapeamento concluído com sucesso. Email: {}, Nome: {}, Projeto: {}, Ativo: true",
                responsavel.getEmail(), responsavel.getNomeCompleto(), responsavel.getNomeDoProjeto());

        return responsavel;
    }
}
