package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.UnidadeRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UnidadeResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import com.enterprise.raizesnordeste.domain.mapper.UnidadeMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.UnidadeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final UnidadeMapper unidadeMapper;

    public Page<UnidadeResponseDTO> getAll(Pageable pageable) {
        return unidadeRepository.findAll(pageable).map(unidadeMapper::toUnidadeResponseDTO);
    }

    public UnidadeResponseDTO getUnidadeById(Long id) {
        return unidadeMapper.toUnidadeResponseDTO(buscarUnidade(id));
    }

    @Transactional
    public UnidadeResponseDTO createUnidade(UnidadeRequestDTO request) {
        if (unidadeRepository.existsByCnpj(request.cnpj())) {
            throw new BusinessException("CNPJ já cadastrado");
        }

        var unidade = unidadeMapper.toUnidade(request);
        unidadeRepository.save(unidade);
        return unidadeMapper.toUnidadeResponseDTO(unidade);
    }

    @Transactional
    public UnidadeResponseDTO updateUnidade(Long id, UnidadeRequestDTO request) {
        var unidade = buscarUnidade(id);

        if (!unidade.getCnpj().equals(request.cnpj()) &&
                unidadeRepository.existsByCnpj(request.cnpj())) {
            throw new BusinessException("CNPJ já cadastrado");
        }

        unidade.setNomeFantasia(request.nomeFantasia());
        unidade.setCnpj(request.cnpj());
        unidade.setEndereco(request.endereco());
        unidade.setContato(request.contato());
        unidade.setRegiao(request.regiao());
        unidade.setEstado(request.estado());

        unidadeRepository.save(unidade);
        return unidadeMapper.toUnidadeResponseDTO(unidade);
    }

    @Transactional
    public void desativar(Long id) {
        var unidade = buscarUnidade(id);

        if (!unidade.getStatus()) {
            throw new BusinessException("Unidade já está inativa");
        }

        unidade.setStatus(false);
        unidadeRepository.save(unidade);
    }

    private Unidade buscarUnidade(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + id));
    }
}
