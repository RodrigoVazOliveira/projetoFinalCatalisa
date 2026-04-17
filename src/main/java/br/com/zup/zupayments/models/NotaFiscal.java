package br.com.zup.zupayments.models;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notas_fiscais")
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false)
    private Long numeroDaNota;

    @ManyToOne
    private Fornecedor fornecedor;

    private Double valorAPagar;

    private LocalDate dataDeEmissao;

    @ManyToMany
    private List<PedidoDeCompra> pedidoDeCompra;

    private LocalDate dataDeEnvio;

    @ManyToOne
    private Responsavel responsavel;

    private Boolean cancelar;

    public NotaFiscal() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getNumeroDaNota() {
        return numeroDaNota;
    }

    public void setNumeroDaNota(Long numeroDaNota) {
        this.numeroDaNota = numeroDaNota;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Double getValorAPagar() {
        return valorAPagar;
    }

    public void setValorAPagar(Double valorAPagar) {
        this.valorAPagar = valorAPagar;
    }

    public LocalDate getDataDeEmissao() {
        return dataDeEmissao;
    }

    public void setDataDeEmissao(LocalDate dataDeEmissao) {
        this.dataDeEmissao = dataDeEmissao;
    }

    public List<PedidoDeCompra> getPedidoDeCompra() {
        return pedidoDeCompra;
    }

    public void setPedidoDeCompra(List<PedidoDeCompra> pedidoDeCompra) {
        this.pedidoDeCompra = pedidoDeCompra;
    }

    public LocalDate getDataDeEnvio() {
        return dataDeEnvio;
    }

    public void setDataDeEnvio(LocalDate dataDeEnvio) {
        this.dataDeEnvio = dataDeEnvio;
    }

    public Responsavel getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getCancelar() {
        return cancelar;
    }

    public void setCancelar(Boolean cancelar) {
        this.cancelar = cancelar;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscal that = (NotaFiscal) o;
        return Objects.equals(id, that.id) && Objects.equals(numeroDaNota, that.numeroDaNota) && Objects.equals(fornecedor, that.fornecedor) && Objects.equals(valorAPagar, that.valorAPagar) && Objects.equals(dataDeEmissao, that.dataDeEmissao) && Objects.equals(pedidoDeCompra, that.pedidoDeCompra) && Objects.equals(dataDeEnvio, that.dataDeEnvio) && Objects.equals(responsavel, that.responsavel) && Objects.equals(cancelar, that.cancelar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroDaNota, fornecedor, valorAPagar, dataDeEmissao, pedidoDeCompra, dataDeEnvio, responsavel, cancelar);
    }

    @Override
    public String toString() {
        return "NotaFiscal{" +
                "id=" + id +
                ", numeroDaNota=" + numeroDaNota +
                ", fornecedor=" + fornecedor +
                ", valorAPagar=" + valorAPagar +
                ", dataDeEmissao=" + dataDeEmissao +
                ", pedidoDeCompra=" + pedidoDeCompra +
                ", dataDeEnvio=" + dataDeEnvio +
                ", responsavel=" + responsavel +
                ", cancelar=" + cancelar +
                '}';
    }
}
