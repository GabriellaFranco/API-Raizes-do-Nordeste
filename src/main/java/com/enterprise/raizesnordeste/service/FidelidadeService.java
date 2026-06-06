package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.response.FidelidadeResponseDTO;
import com.enterprise.raizesnordeste.domain.mapper.FidelidadeMapper;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.ClienteRepository;
import com.enterprise.raizesnordeste.repository.FidelidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final ClienteRepository clienteRepository;
    private final FidelidadeMapper fidelidadeMapper;

    public FidelidadeResponseDTO getPorCliente(Long idCliente) {
        clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + idCliente));

        return fidelidadeRepository.findByClienteId(idCliente).map(fidelidadeMapper::toFidelidadeResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Fidelidade não encontrada para cliente: " + idCliente));
    }

    public Page<FidelidadeResponseDTO> getAll(Pageable pageable) {
        return fidelidadeRepository.findAll(pageable).map(fidelidadeMapper::toFidelidadeResponseDTO);
    }

    public List<FidelidadeResponseDTO> buscarRanking() {
        return fidelidadeRepository.findTop10ByOrderByPontosAcumuladosDesc().stream()
                .map(fidelidadeMapper::toFidelidadeResponseDTO).toList();
    }
}
