package br.com.zup.zupayments.services;

import br.com.zup.zupayments.exceptions.erros.FornecedorCadastradoException;
import br.com.zup.zupayments.exceptions.erros.FornecedorNaoCadastrado;
import br.com.zup.zupayments.models.Fornecedor;
import br.com.zup.zupayments.repositories.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public Fornecedor cadastrarFornecedor (Fornecedor fornecedor) {
        try {
            return fornecedorRepository.save(fornecedor);
        } catch (Exception error) {
            throw new FornecedorCadastradoException("");
        }
    }

    public Fornecedor pesquisarFornecedorPorCnpjOuCpf(String cnpjOuCpf){
        Optional<Fornecedor> optionalFornecedor = fornecedorRepository.findById(cnpjOuCpf);

        if (optionalFornecedor.isPresent()){
            return optionalFornecedor.get();
        }

        throw new FornecedorNaoCadastrado("");
    }

    public Fornecedor atualizarCadastroFornecedor (String id, Fornecedor fornecedor){

        Optional<Fornecedor> fornecedorOptional = fornecedorRepository.findById(id);

        if (!fornecedorOptional.isPresent())
            throw new FornecedorNaoCadastrado("");

        fornecedor.setCnpjOuCpf(id);
        fornecedorRepository.save(fornecedor);

        return fornecedor;
    }

    public void ativarOuDesativarFornecedor(String cnpjOuCpf) {
        Fornecedor fornecedor = pesquisarFornecedorPorCnpjOuCpf(cnpjOuCpf);
        fornecedor.setAtivo(!fornecedor.getAtivo());
        fornecedorRepository.save(fornecedor);
    }
}