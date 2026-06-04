package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.PerfilAutoridade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilAutoridadeRepository extends JpaRepository<PerfilAutoridade, Long>,
        JpaSpecificationExecutor<PerfilAutoridade> {

    Optional<PerfilAutoridade> findByNome(String nome);
    boolean existsByNome(String nome);
}