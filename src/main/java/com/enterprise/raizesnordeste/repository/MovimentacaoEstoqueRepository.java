package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.MovimentacaoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long>,
        JpaSpecificationExecutor<MovimentacaoEstoque> {

    Page<MovimentacaoEstoque> findAllByEstoqueId(Long estoqueId, Pageable pageable);
    Page<MovimentacaoEstoque> findAllByUsuarioId(Long usuarioId, Pageable pageable);
    Page<MovimentacaoEstoque> findAllByEstoqueUnidadeId(Long unidadeId, Pageable pageable);
}

