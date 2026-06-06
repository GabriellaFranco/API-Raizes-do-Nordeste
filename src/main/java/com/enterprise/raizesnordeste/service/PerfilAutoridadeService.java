package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.PerfilAutoridadeRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PerfilAutoridadeResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.PerfilAutoridade;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.PerfilAutoridadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PerfilAutoridadeService {


    private final PerfilAutoridadeRepository perfilAutoridadeRepository;

    public Page<PerfilAutoridadeResponseDTO> getAll(Pageable pageable) {
        return perfilAutoridadeRepository.findAll(pageable).map(this::toPerfilAutoridadeResponseDTO);
    }

    public PerfilAutoridadeResponseDTO getPerfilById(Long id) {
        return toPerfilAutoridadeResponseDTO(buscarPerfil(id));
    }

    @Transactional
    public PerfilAutoridadeResponseDTO createPerfil(PerfilAutoridadeRequestDTO request) {
        if (perfilAutoridadeRepository.existsByNome(request.nome())) {
            throw new BusinessException("Perfil já cadastrado: " + request.nome());
        }

        var perfil = PerfilAutoridade.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .status(true)
                .build();

        perfilAutoridadeRepository.save(perfil);
        return toPerfilAutoridadeResponseDTO(perfil);
    }

    @Transactional
    public PerfilAutoridadeResponseDTO updatePerfil(Long id, PerfilAutoridadeRequestDTO request) {
        var perfil = buscarPerfil(id);

        if (!perfil.getNome().equals(request.nome()) &&
                perfilAutoridadeRepository.existsByNome(request.nome())) {
            throw new BusinessException("Perfil já cadastrado: " + request.nome());
        }

        perfil.setNome(request.nome());
        perfil.setDescricao(request.descricao());

        perfilAutoridadeRepository.save(perfil);
        return toPerfilAutoridadeResponseDTO(perfil);
    }

    @Transactional
    public void alterarStatus(Long id) {
        var perfil = buscarPerfil(id);
        perfil.setStatus(!perfil.isStatus());
        perfilAutoridadeRepository.save(perfil);
    }

    private PerfilAutoridade buscarPerfil(Long id) {
        return perfilAutoridadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado: " + id));
    }

    private PerfilAutoridadeResponseDTO toPerfilAutoridadeResponseDTO(PerfilAutoridade perfil) {
        return PerfilAutoridadeResponseDTO.builder()
                .id(perfil.getId())
                .nome(perfil.getNome())
                .descricao(perfil.getDescricao())
                .status(perfil.isStatus())
                .createdAt(perfil.getCreatedAt())
                .updatedAt(perfil.getUpdatedAt())
                .createdBy(perfil.getCreatedBy())
                .updatedBy(perfil.getUpdatedBy())
                .build();
    }
}

