package br.com.zup.zupayments.services;

import br.com.zup.zupayments.exceptions.erros.FornecedorCadastradoException;
import br.com.zup.zupayments.exceptions.erros.FornecedorNaoCadastrado;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.repositories.FornecedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FornecedorService {

    private static final Logger logger = LoggerFactory.getLogger(FornecedorService.class);

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public Fornecedor cadastrarFornecedor (Fornecedor fornecedor) {
        logger.info("Iniciando cadastro de fornecedor com CNPJ/CPF: {}", fornecedor.getCnpjOuCpf());

        if (fornecedorRepository.existsByCnpjOuCpf(fornecedor.getCnpjOuCpf())) {
            logger.warn("Tentativa de cadastro de fornecedor duplicado: {}", fornecedor.getCnpjOuCpf());
            throw new FornecedorCadastradoException("Fornecedor com CNPJ/CPF " + fornecedor.getCnpjOuCpf() + " já cadastrado");
        }

        Fornecedor fornecedorCadastrado = fornecedorRepository.save(fornecedor);
        logger.info("Fornecedor cadastrado com sucesso: {} - {}", fornecedor.getCnpjOuCpf(), fornecedor.getRazaoSocial());
        return fornecedorCadastrado;
    }

    public Fornecedor pesquisarFornecedorPorCnpjOuCpf(String cnpjOuCpf){
        logger.debug("Pesquisando fornecedor com CNPJ/CPF: {}", cnpjOuCpf);
        Optional<Fornecedor> optionalFornecedor = fornecedorRepository.findById(cnpjOuCpf);

        if (optionalFornecedor.isPresent()){
            logger.debug("Fornecedor encontrado: {} - {}", cnpjOuCpf, optionalFornecedor.get().getRazaoSocial());
            return optionalFornecedor.get();
        }

        logger.warn("Fornecedor não encontrado: {}", cnpjOuCpf);
        throw new FornecedorNaoCadastrado("Fornecedor não foi cadastrado");
    }

    public Fornecedor atualizarCadastroFornecedor ( Fornecedor fornecedor){
        logger.info("Iniciando atualização de cadastro do fornecedor: {}", fornecedor.getCnpjOuCpf());
        Fornecedor fornecedorBD = pesquisarFornecedorPorCnpjOuCpf(fornecedor.getCnpjOuCpf());

        fornecedorBD.setRazaoSocial(fornecedor.getRazaoSocial());
        fornecedorBD.setBairro(fornecedor.getBairro());
        fornecedorBD.setCep(fornecedor.getCep());
        fornecedorBD.setCategoriaDeCusto(fornecedor.getCategoriaDeCusto());
        fornecedorBD.setCidade(fornecedor.getCidade());
        fornecedorBD.setEmail(fornecedor.getEmail());
        fornecedorBD.setEstado(fornecedor.getEstado());
        fornecedorBD.setLogradouro(fornecedor.getLogradouro());
        fornecedorBD.setNumero(fornecedor.getNumero());
        fornecedorBD.setTelefone(fornecedor.getTelefone());

        Fornecedor fornecedorAtualizado = fornecedorRepository.save(fornecedorBD);
        logger.info("Cadastro do fornecedor atualizado com sucesso: {}", fornecedor.getCnpjOuCpf());
        return fornecedorAtualizado;
    }

    public void ativarOuDesativarFornecedor(String cnpjOuCpf) {
        logger.info("Iniciando ativação/desativação do fornecedor: {}", cnpjOuCpf);
        Fornecedor fornecedor = pesquisarFornecedorPorCnpjOuCpf(cnpjOuCpf);
        boolean novoStatus = !fornecedor.getAtivo();
        fornecedor.setAtivo(novoStatus);
        fornecedorRepository.save(fornecedor);
        logger.info("Fornecedor {} agora está: {}", cnpjOuCpf, novoStatus ? "ATIVO" : "INATIVO");
    }
}