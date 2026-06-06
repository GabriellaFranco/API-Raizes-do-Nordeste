package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardapioRepository extends JpaRepository<Cardapio, Long>, JpaSpecificationExecutor<Cardapio> {

    Optional<Cardapio> findByUnidadeIdAndAtivoTrue(Long unidadeId);
    List<Cardapio> findAllByUnidadeId(Long unidadeId);
    boolean existsByUnidadeIdAndAtivoTrue(Long unidadeId);
}
