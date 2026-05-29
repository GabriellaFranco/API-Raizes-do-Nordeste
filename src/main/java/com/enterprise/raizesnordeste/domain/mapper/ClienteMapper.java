package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.ClienteRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ClienteResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toCliente(ClienteRequestDTO dto, Usuario usuario) {
        return Cliente.builder()
                .usuario(usuario)
                .dataNascimento(dto.dataNascimento())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .build();
    }

    public ClienteResponseDTO toClienteResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getUsuario().getNome())
                .cpf(cliente.getUsuario().getCpf())
                .email(cliente.getUsuario().getEmail())
                .dataNascimento(cliente.getDataNascimento())
                .endereco(cliente.getEndereco())
                .telefone(cliente.getTelefone())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .createdBy(cliente.getCreatedBy())
                .statusConsentimento(cliente.getStatusConsentimento())
                .dataConsentimento(cliente.getDataConsentimento())
                .dataRevogacao(cliente.getDataRevogacao())
                .anonimizado(cliente.getAnonimizado())
                .build();
    }
}
