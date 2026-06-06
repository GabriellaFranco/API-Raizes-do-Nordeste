package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.RegistroRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.UsuarioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UsuarioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.PerfilAutoridade;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toUsuario(UsuarioRequestDTO dto, Unidade unidade, PerfilAutoridade perfil) {
        return Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .senha(dto.senha())
                .unidade(unidade)
                .perfis(Set.of(perfil))
                .build();
    }

    public Usuario toEntity(RegistroRequestDTO dto) {
        return Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .cpf(dto.cpf())
                .senha(dto.senha())
                .build();
    }

    public UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .email(usuario.getEmail())
                .status(usuario.getStatus())
                .createdAt(usuario.getCreatedAt())
                .createdBy(usuario.getCreatedBy())
                .updatedAt(usuario.getUpdatedAt())
                .updatedBy(usuario.getUpdatedBy())
                .perfis(usuario.getPerfis().stream().map(PerfilAutoridade::getNome)
                        .collect(Collectors.toSet()))
                .build();
    }
}
