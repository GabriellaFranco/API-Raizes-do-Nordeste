package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long>, JpaSpecificationExecutor<Estoque> {

    Optional<Estoque> findByUnidadeIdAndItemId(Long unidadeId, Long itemId);
    Page<Estoque> findAllByUnidadeId(Long unidadeId, Pageable pageable);
    List<Estoque> findAllByUnidadeIdAndQuantidadeLessThanEqual(Long unidadeId, Integer quantidadeMinima);
    boolean existsByUnidadeIdAndItemId(Long unidadeId, Long itemId);
}
