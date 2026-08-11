package br.com.zup.zupayments.models;

import br.com.zup.zupayments.enums.CategoriaDeCusto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @Column(length = 25, nullable = false)
    private String cnpjOuCpf;

    @Column(length = 100, nullable = false)
    private String razaoSocial;

    @Column(length = 80, nullable = false)
    private String logradouro;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 60, nullable = false)
    private String bairro;

    @Column(length = 80, nullable = false)
    private String cidade;

    @Column(length = 25, nullable = false)
    private String estado;

    @Column(length = 15, nullable = false)
    private String cep;

    @Column(length = 25, nullable = false)
    private String telefone;

    @Column(length = 80, nullable = false)
    private String email;

    @Column(nullable = false)
    private CategoriaDeCusto categoriaDeCusto;

    @Column(nullable = false)
    private Boolean ativo;

    public String getCnpjOuCpf() {
        return cnpjOuCpf;
    }

    public void setCnpjOuCpf(String cnpjOuCpf) {
        this.cnpjOuCpf = cnpjOuCpf;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CategoriaDeCusto getCategoriaDeCusto() {
        return categoriaDeCusto;
    }

    public void setCategoriaDeCusto(CategoriaDeCusto categoriaDeCusto) {
        this.categoriaDeCusto = categoriaDeCusto;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
