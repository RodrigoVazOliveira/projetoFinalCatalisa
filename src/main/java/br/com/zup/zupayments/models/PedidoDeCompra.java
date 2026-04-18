package br.com.zup.zupayments.models;

import br.com.zup.zupayments.enums.FormaDePagamento;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pedidos_de_compras")
public class PedidoDeCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID numeroDePedido;

    @Column(nullable = false)
    private LocalDate dataDeVencimento;

    @Column(nullable = false)
    private Double saldo;

    @Column(nullable = false)
    private LocalDate dataDePagamento;

    @ManyToOne
    private Responsavel responsavel;

    @Column(nullable = false)
    private LocalDate dataLimiteEnvio;

    @Column(nullable = false)
    private FormaDePagamento formaDePagamento;

    @ManyToOne
    private Fornecedor fornecedor;

    @Column(nullable = false)
    private Boolean cancelado;

    public PedidoDeCompra() {
    }

    public UUID getNumeroDePedido() {
        return numeroDePedido;
    }

    public void setNumeroDePedido(UUID numeroDePedido) {
        this.numeroDePedido = numeroDePedido;
    }

    public LocalDate getDataDeVencimento() {
        return dataDeVencimento;
    }

    public void setDataDeVencimento(LocalDate dataDeVencimento) {
        this.dataDeVencimento = dataDeVencimento;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public LocalDate getDataDePagamento() {
        return dataDePagamento;
    }

    public void setDataDePagamento(LocalDate dataDePagamento) {
        this.dataDePagamento = dataDePagamento;
    }

    public Responsavel getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
    }

    public LocalDate getDataLimiteEnvio() {
        return dataLimiteEnvio;
    }

    public void setDataLimiteEnvio(LocalDate dataLimiteEnvio) {
        this.dataLimiteEnvio = dataLimiteEnvio;
    }

    public FormaDePagamento getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(FormaDePagamento formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PedidoDeCompra that = (PedidoDeCompra) o;
        return Objects.equals(numeroDePedido, that.numeroDePedido) && Objects.equals(dataDeVencimento, that.dataDeVencimento) && Objects.equals(saldo, that.saldo) && Objects.equals(dataDePagamento, that.dataDePagamento) && Objects.equals(responsavel, that.responsavel) && Objects.equals(dataLimiteEnvio, that.dataLimiteEnvio) && formaDePagamento == that.formaDePagamento && Objects.equals(fornecedor, that.fornecedor) && Objects.equals(cancelado, that.cancelado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroDePedido, dataDeVencimento, saldo, dataDePagamento, responsavel, dataLimiteEnvio, formaDePagamento, fornecedor, cancelado);
    }
}
