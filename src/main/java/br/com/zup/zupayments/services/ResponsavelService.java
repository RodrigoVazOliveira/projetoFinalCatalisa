package br.com.zup.zupayments.services;

import br.com.zup.zupayments.exceptions.erros.ResponsavelJaCadastradoException;
import br.com.zup.zupayments.exceptions.erros.ResponsavelNaoExisteException;
import br.com.zup.zupayments.models.Responsavel;
import br.com.zup.zupayments.repositories.ResponsavelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponsavelService {
    private static final Logger log = LoggerFactory.getLogger(ResponsavelService.class);
    private final ResponsavelRepository responsavelRepository;

    public ResponsavelService(ResponsavelRepository responsavelRepository) {
        this.responsavelRepository = responsavelRepository;
    }

    public Responsavel cadastrarResponsavel(Responsavel responsavel) {
        log.info("Iniciando cadastro de novo responsável com email: {}", responsavel.getEmail());

        if (responsavelRepository.existsByEmail(responsavel.getEmail())) {
            log.warn("Tentativa de cadastro de responsável com email duplicado: {}", responsavel.getEmail());
            throw new ResponsavelJaCadastradoException("Um responsável com e-mail " + responsavel.getEmail() + " já foi cadastrado!");
        }

        Responsavel responsavelSalvo = responsavelRepository.save(responsavel);
        log.info("Responsável cadastrado com sucesso. Email: {}, Nome: {}", responsavelSalvo.getEmail(), responsavelSalvo.getNomeCompleto());
        return responsavelSalvo;
    }

    public Responsavel procurarResponsavelPorEmail(String email) {
        log.info("Buscando responsável com email: {}", email);
        Optional<Responsavel> optionalResponsavel = responsavelRepository.findById(email);

        if (optionalResponsavel.isEmpty()) {
            log.warn("Responsável não encontrado com email: {}", email);
            throw new ResponsavelNaoExisteException("Não existe responsável com e-mail" + email);
        }

        log.info("Responsável encontrado com sucesso. Email: {}", email);
        return optionalResponsavel.get();
    }

    public void ativarOuDesativarResponsavel(String emailResponsavel) {
        log.info("Iniciando alternância de status do responsável com email: {}", emailResponsavel);

        Responsavel responsavelAtual = procurarResponsavelPorEmail(emailResponsavel);
        Boolean statusAnterior = responsavelAtual.getAtivo();

        responsavelAtual.setAtivo(!responsavelAtual.getAtivo());
        responsavelRepository.save(responsavelAtual);

        log.info("Status do responsável alterado com sucesso. Email: {}, Status anterior: {}, Status novo: {}",
                emailResponsavel, statusAnterior, responsavelAtual.getAtivo());
    }
}
