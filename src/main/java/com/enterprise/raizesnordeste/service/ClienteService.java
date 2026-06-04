package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.ClienteRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ClienteResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.mapper.ClienteMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public Page<ClienteResponseDTO> getAll(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(clienteMapper::toClienteResponseDTO);
    }

    public ClienteResponseDTO getClienteById(Long id) {
        return clienteMapper.toClienteResponseDTO(buscarCliente(id));
    }

    @Transactional
    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO request) {
        var cliente = buscarCliente(id);

        cliente.setDataNascimento(request.dataNascimento());
        cliente.setTelefone(request.telefone());
        cliente.setEndereco(request.endereco());

        clienteRepository.save(cliente);
        return clienteMapper.toClienteResponseDTO(cliente);
    }

    @Transactional
    public void revogarConsentimento(Long id) {
        var cliente = buscarCliente(id);

        if (!cliente.getStatusConsentimento()) {
            throw new BusinessException("Cliente já revogou o consentimento");
        }

        cliente.setStatusConsentimento(false);
        cliente.setDataRevogacao(LocalDateTime.now());
        cliente.setAnonimizado(true);
        cliente.getUsuario().setNome("Usuário Anonimizado");
        cliente.getUsuario().setEmail("anonimizado_" + cliente.getId() + "@anonimizado.com");
        cliente.getUsuario().setCpf("00000000000");
        cliente.setTelefone(null);
        cliente.setEndereco(null);
        cliente.setDataNascimento(null);

        clienteRepository.save(cliente);
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }
}
