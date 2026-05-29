package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.response.AuditoriaLoginResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.AuditoriaLogin;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaLoginMapper {

    public AuditoriaLogin toAuditoriaLogin(Usuario usuario, String ip, Boolean sucesso) {
        return AuditoriaLogin.builder()
                .usuario(usuario)
                .ip(ip)
                .sucesso(sucesso)
                .build();
    }

    public AuditoriaLoginResponseDTO toResponse(AuditoriaLogin auditoriaLogin) {
        return AuditoriaLoginResponseDTO.builder()
                .id(auditoriaLogin.getId())
                .idUsuario(auditoriaLogin.getUsuario() != null ? auditoriaLogin.getUsuario().getId() : null)
                .nomeUsuario(auditoriaLogin.getUsuario() != null ? auditoriaLogin.getUsuario().getNome() : null)
                .email(auditoriaLogin.getUsuario() != null ? auditoriaLogin.getUsuario().getEmail() : null)
                .ip(auditoriaLogin.getIp())
                .sucesso(auditoriaLogin.getSucesso())
                .dataHora(auditoriaLogin.getDataHora())
                .build();
    }
}
