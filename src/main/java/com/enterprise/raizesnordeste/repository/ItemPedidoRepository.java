package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.ItemPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long>, JpaSpecificationExecutor<ItemPedido> {

    Page<ItemPedido> findAllByPedidoId(Long pedidoId, Pageable pageable);
}
