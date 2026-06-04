package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    Optional<Cliente> findByUsuarioEmail(String usuarioId);
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}
