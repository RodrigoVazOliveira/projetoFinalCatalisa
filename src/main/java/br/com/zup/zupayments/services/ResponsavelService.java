package br.com.zup.zupayments.services;

import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponsavelService {

    @Autowired
    private ResponsavelRepository responsavelRepository;

    public Responsavel cadastrarResponsavel (Responsavel responsavel){
        try{
            Responsavel objResponsavel = responsavelRepository.save(responsavel);
            return objResponsavel;
        }catch (Exception error){
            throw new RuntimeException("Responsável já cadastrado");
        }
    }

    public Responsavel procurarResponsavelPorEmail(String email) {
        Optional<Responsavel> optionalResponsavel = responsavelRepository.findById(email);

        if (optionalResponsavel.isEmpty()) {
            throw new RuntimeException("Não existe responsável com e-mail " + email);
        }

        return optionalResponsavel.get();
    }

    public Responsavel ativarOuDesativarResponasvel(String emailResponsavel) {
        Responsavel responsavelAtual = procurarResponsavelPorEmail(emailResponsavel);

        responsavelAtual.setAtivo(!responsavelAtual.getAtivo());

        return responsavelRepository.save(responsavelAtual);
    }
}
