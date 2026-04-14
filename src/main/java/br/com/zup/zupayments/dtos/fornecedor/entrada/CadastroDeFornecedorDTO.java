package br.com.zup.zupayments.dtos.fornecedor.entrada;

import br.com.zup.zupayments.enums.CategoriaDeCusto;
import br.com.zup.zupayments.models.Fornecedor;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

public record CadastroDeFornecedorDTO(
        @CPF(message = "{validacao.cpf_invalido}")
        String cpf,

        @CNPJ(message = "{validacao.cnpj_invalido}")
        String cnpj,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 100, message = "{validacao.tamanho_campo}")
        String razaoSocial,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 80, message = "{validacao.tamanho_campo}")
        String logradouro,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 10, message = "{validacao.tamanho_campo}")
        String numero,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 60, message = "{validacao.tamanho_campo}")
        String bairro,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 80, message = "{validacao.tamanho_campo}")
        String cidade,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 25, message = "{validacao.tamanho_campo}")
        String estado,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 15, message = "{validacao.tamanho_campo}")
        String cep,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 25, message = "{validacao.tamanho_campo}")
        String telefone,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        @NotBlank(message = "{validacao.campo_em_branco}")
        @NotEmpty(message = "{validacao.campo_vazio}")
        @Size(max = 80, message = "{validacao.tamanho_campo}")
        @Email(message = "{validacao.email_invalido}")
        String email,

        @NotNull(message = "{validacao.campo_obrigatorio}")
        CategoriaDeCusto categoriaDeCusto
) {
    public Fornecedor converterDtoParaModelo() {
        String cnpjOuCpf = null;

        if (this.cpf != null) {
            cnpjOuCpf = cpf;
        }

        if (this.cnpj != null) {
            cnpjOuCpf = cnpj;
        }

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCnpjOuCpf(cnpjOuCpf);
        fornecedor.setRazaoSocial(this.razaoSocial);
        fornecedor.setLogradouro(this.logradouro);
        fornecedor.setNumero(this.numero);
        fornecedor.setBairro(this.bairro);
        fornecedor.setCidade(this.cidade);
        fornecedor.setEstado(this.estado);
        fornecedor.setCep(this.cep);
        fornecedor.setTelefone(this.telefone);
        fornecedor.setEmail(this.email);
        fornecedor.setCategoriaDeCusto(this.categoriaDeCusto);
        fornecedor.setAtivo(true);

        return fornecedor;
    }
}
