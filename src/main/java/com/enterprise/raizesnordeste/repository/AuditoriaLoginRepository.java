package com.enterprise.raizesnordeste.repository;

import com.enterprise.raizesnordeste.domain.entity.AuditoriaLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaLoginRepository extends JpaRepository<AuditoriaLogin, Long>, JpaSpecificationExecutor<AuditoriaLogin> {

    Page<AuditoriaLogin> findAllByUsuarioId(Long usuarioId, Pageable pageable);
    Page<AuditoriaLogin> findAllByIp(String ip, Pageable pageable);
    Page<AuditoriaLogin> findAllByUsuarioIdAndSucessoFalse(Long usuarioId, Pageable pageable);
    Page<AuditoriaLogin> findAllByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}