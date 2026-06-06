package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Pedido;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    Page<Pedido> findAllByClienteId(Long clienteId, Pageable pageable);
    Page<Pedido> findAllByUnidadeId(Long unidadeId, Pageable pageable);
    List<Pedido> findAllByUnidadeIdAndStatusNotIn(Long unidadeId, List<StatusPedido> status);
    List<Pedido> findAllByUnidadeIdAndStatusNotAndCreatedAtBetween(Long unidadeId, StatusPedido status, LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findAllByStatusNotAndCreatedAtBetween(StatusPedido status, LocalDateTime inicio, LocalDateTime fim);
}
