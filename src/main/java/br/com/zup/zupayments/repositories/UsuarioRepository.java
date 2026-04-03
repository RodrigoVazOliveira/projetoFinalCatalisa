package br.com.zup.zupayments.repositories;

import br.com.zup.zupayments.models.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);
}