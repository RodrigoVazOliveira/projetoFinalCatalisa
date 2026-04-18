package br.com.zup.zupayments.repositories;

import br.com.zup.zupayments.models.NotaFiscal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface NotaFiscalRepository extends CrudRepository<NotaFiscal, UUID> {
    Iterable<NotaFiscal> findAllByDataDeEmissaoBetween(LocalDate dataInicial, LocalDate dataFinal);
}