package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.ClienteRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.UpdateClienteDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ClienteResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.ClienteMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock private ClienteRepository clienteRepository;
    @Mock private ClienteMapper clienteMapper;

    private Cliente cliente;
    private Usuario usuario;
    private ClienteResponseDTO clienteResponse;
    private ClienteRequestDTO clienteRequest;
    private UpdateClienteDTO updateRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Ana Lima")
                .email("ana@email.com")
                .cpf("52998224725")
                .senha("senhaCriptografada")
                .status(true)
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .dataNascimento(LocalDate.of(1995, 5, 10))
                .telefone("47999999999")
                .endereco("Rua Teste, 123")
                .statusConsentimento(true)
                .anonimizado(false)
                .build();

        clienteResponse = new ClienteResponseDTO(
                1L, "Ana Lima", "ana@email.com", "52998224725",
                LocalDate.of(1995, 5, 10), "47999999999",
                "Rua Teste, 123", true, LocalDateTime.now(),
                null, false, null, null, null, null
        );

        clienteRequest = new ClienteRequestDTO(
                LocalDate.of(1995, 5, 10),
                "47988888888",
                "Rua Nova, 456"
        );

        updateRequest = new UpdateClienteDTO("4790005432", "Rua velha 123");
    }

    @Test
    @DisplayName("Listar - deve retornar página de clientes")
    void listar_deveRetornarPaginaDeClientes() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(cliente));

        when(clienteRepository.findAll(pageable)).thenReturn(page);
        when(clienteMapper.toClienteResponseDTO(any())).thenReturn(clienteResponse);

        var result = clienteService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(clienteRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Buscar por ID - deve retornar cliente")
    void buscarPorId_deveRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toClienteResponseDTO(any())).thenReturn(clienteResponse);

        var result = clienteService.getClienteById(1L);

        assertNotNull(result);
        assertEquals("Ana Lima", result.nome());
        verify(clienteRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar por ID - deve lançar exceção para cliente não encontrado")
    void buscarPorId_deveLancarExcecaoParaClienteNaoEncontrado() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.getClienteById(99L));
    }

    @Test
    @DisplayName("Atualizar - deve atualizar dados do cliente com sucesso")
    void atualizar_deveAtualizarComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any())).thenReturn(cliente);
        when(clienteMapper.toClienteResponseDTO(any())).thenReturn(clienteResponse);

        var result = clienteService.updateCliente(1L, updateRequest);

        assertNotNull(result);
        verify(clienteRepository).save(any());
    }

    @Test
    @DisplayName("Atualizar - deve lançar exceção para cliente não encontrado")
    void atualizar_deveLancarExcecaoParaClienteNaoEncontrado() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.updateCliente(99L, updateRequest));

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Revogar consentimento - deve revogar e anonimizar com sucesso")
    void revogarConsentimento_deveRevogarEAnonimizarComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any())).thenReturn(cliente);

        clienteService.revogarConsentimento(1L);

        assertFalse(cliente.getStatusConsentimento());
        assertTrue(cliente.getAnonimizado());
        assertNotNull(cliente.getDataRevogacao());
        assertEquals("Usuário Anonimizado", cliente.getUsuario().getNome());
        assertEquals("ANON0000001", cliente.getUsuario().getCpf());
        assertNull(cliente.getTelefone());
        assertNull(cliente.getEndereco());
        assertNull(cliente.getDataNascimento());
        verify(clienteRepository).save(any());
    }

    @Test
    @DisplayName("Revogar consentimento - deve lançar exceção para consentimento já revogado")
    void revogarConsentimento_deveLancarExcecaoParaConsentimentoJaRevogado() {
        cliente.setStatusConsentimento(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(BusinessException.class,
                () -> clienteService.revogarConsentimento(1L));

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Revogar consentimento - deve lançar exceção para cliente não encontrado")
    void revogarConsentimento_deveLancarExcecaoParaClienteNaoEncontrado() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.revogarConsentimento(99L));

        verify(clienteRepository, never()).save(any());
    }
}