package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long>, JpaSpecificationExecutor<Fidelidade> {

    Optional<Fidelidade> findByClienteId(Long clienteId);
    boolean existsByClienteId(Long clienteId);
}
