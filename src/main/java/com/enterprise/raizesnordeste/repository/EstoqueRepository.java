package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long>, JpaSpecificationExecutor<Estoque> {

    Optional<Estoque> findByUnidadeIdAndItemId(Long unidadeId, Long itemId);
    Page<Estoque> findAllByUnidadeId(Long unidadeId, Pageable pageable);
    @Query("SELECT e FROM Estoque e WHERE e.unidade.id = :idUnidade AND e.quantidade <= e.quantidadeMinima")
    List<Estoque> findEstoqueCritico(@Param("idUnidade") Long idUnidade);}
