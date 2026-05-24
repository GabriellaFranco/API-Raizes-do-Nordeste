package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long>, JpaSpecificationExecutor<Unidade> {

    Optional<Unidade> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}
