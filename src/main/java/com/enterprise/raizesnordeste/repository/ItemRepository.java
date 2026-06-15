package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.Item;
import com.enterprise.raizesnordeste.domain.enuns.CategoriaItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    Page<Item> findAll(Pageable pageable);
    boolean existsByNomeAndCategoria(String nome, CategoriaItem categoria);
}
