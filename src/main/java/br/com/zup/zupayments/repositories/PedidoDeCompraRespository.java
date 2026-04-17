package br.com.zup.zupayments.repositories;

import br.com.zup.zupayments.models.PedidoDeCompra;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PedidoDeCompraRespository extends CrudRepository<PedidoDeCompra, UUID> {
    Iterable<PedidoDeCompra> findAllByResponsavelAtivo(Boolean ativo);
    Iterable<PedidoDeCompra> findAllBySaldoGreaterThanAndResponsavelAtivo(Double valorMinimo, Boolean ativo);
}