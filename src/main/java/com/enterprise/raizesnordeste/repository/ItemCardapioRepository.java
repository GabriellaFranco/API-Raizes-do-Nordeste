package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.ItemCardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Long>, JpaSpecificationExecutor<ItemCardapio> {

    List<ItemCardapio> findAllByCardapioId(Long cardapioId);
    List<ItemCardapio> findAllByCardapioIdAndDisponivelTrue(Long cardapioId);
    Optional<ItemCardapio> findByCardapioIdAndItemId(Long cardapioId, Long itemId);
    boolean existsByCardapioIdAndItemId(Long cardapioId, Long itemId);
}
