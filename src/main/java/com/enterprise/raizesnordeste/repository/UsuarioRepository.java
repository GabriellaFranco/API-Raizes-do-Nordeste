package com.enterprise.raizesnordeste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByEmail(String email);
    Page<Usuario> findAllByUnidadeId(Long unidadeId, Pageable pageable);
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
