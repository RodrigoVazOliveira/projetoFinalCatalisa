package br.com.zup.zupayments.models;

import br.com.zup.zupayments.enums.RolesEnum;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(length = 50, nullable = false)
    private String nomeCompleto;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private RolesEnum nivelDeAcesso;

    private Boolean ativo;


    @Column(nullable = false, name = "data_hora_criacao")
    @CreationTimestamp
    private LocalDateTime criadoEm;

    @Column(name = "data_hora_atualizacao")
    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public RolesEnum getNivelDeAcesso() {
        return nivelDeAcesso;
    }

    public void setNivelDeAcesso(RolesEnum nivelDeAcesso) {
        this.nivelDeAcesso = nivelDeAcesso;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
