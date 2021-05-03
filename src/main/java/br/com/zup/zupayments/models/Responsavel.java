package br.com.zup.zupayments.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "responsaveis")
public class Responsavel {

    @Id
    private String email;

    @Column(length = 100, nullable = false)
    private String nomeCompleto;

    @Column(length = 70)
    private String nomeDoProjeto;

    public Responsavel() {
    }

    public Responsavel(String email, String nomeCompleto, String nomeDoProjeto) {
        this.email = email;
        this.nomeCompleto = nomeCompleto;
        this.nomeDoProjeto = nomeDoProjeto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeDoProjeto() {
        return nomeDoProjeto;
    }

    public void setNomeDoProjeto(String nomeDoProjeto) {
        this.nomeDoProjeto = nomeDoProjeto;
    }
}
