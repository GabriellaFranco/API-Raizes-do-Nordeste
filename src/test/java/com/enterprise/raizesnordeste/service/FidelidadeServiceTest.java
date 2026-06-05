package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.response.FidelidadeResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.entity.Fidelidade;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.FidelidadeMapper;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.ClienteRepository;
import com.enterprise.raizesnordeste.repository.FidelidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FidelidadeServiceTest {

    @InjectMocks
    private FidelidadeService fidelidadeService;

    @Mock private FidelidadeRepository fidelidadeRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private FidelidadeMapper fidelidadeMapper;

    private Cliente cliente;
    private Usuario usuario;
    private Fidelidade fidelidade;
    private FidelidadeResponseDTO fidelidadeResponse;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Pedro Lima")
                .email("pedro@email.com")
                .status(true)
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .statusConsentimento(true)
                .anonimizado(false)
                .build();

        fidelidade = Fidelidade.builder()
                .id(1L)
                .cliente(cliente)
                .pontosAcumulados(150)
                .totalGasto(new BigDecimal("200.00"))
                .build();

        fidelidadeResponse = FidelidadeResponseDTO.builder()
                .id(1L)
                .idCliente(1L)
                .nomeCliente("Pedro Lima")
                .pontosAcumulados(150)
                .totalGasto(new BigDecimal("200.00"))
                .build();
    }

    @Test
    @DisplayName("Buscar por cliente - deve retornar fidelidade")
    void buscarPorCliente_deveRetornarFidelidade() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(fidelidadeRepository.findByClienteId(1L)).thenReturn(Optional.of(fidelidade));
        when(fidelidadeMapper.toFidelidadeResponseDTO(any())).thenReturn(fidelidadeResponse);

        var result = fidelidadeService.getPorCliente(1L);

        assertNotNull(result);
        assertEquals(150, result.pontosAcumulados());
        assertEquals(new BigDecimal("200.00"), result.totalGasto());
        verify(clienteRepository).findById(1L);
        verify(fidelidadeRepository).findByClienteId(1L);
    }

    @Test
    @DisplayName("Buscar por cliente - deve lançar exceção para cliente não encontrado")
    void buscarPorCliente_deveLancarExcecaoParaClienteNaoEncontrado() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> fidelidadeService.getPorCliente(99L));

        verify(fidelidadeRepository, never()).findByClienteId(anyLong());
    }

    @Test
    @DisplayName("Buscar por cliente - deve lançar exceção para fidelidade não encontrada")
    void buscarPorCliente_deveLancarExcecaoParaFidelidadeNaoEncontrada() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(fidelidadeRepository.findByClienteId(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> fidelidadeService.getPorCliente(1L));
    }

    @Test
    @DisplayName("Listar - deve retornar página de fidelidade")
    void listar_deveRetornarPaginaDeFidelidade() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(fidelidade));

        when(fidelidadeRepository.findAll(pageable)).thenReturn(page);
        when(fidelidadeMapper.toFidelidadeResponseDTO(any())).thenReturn(fidelidadeResponse);

        var result = fidelidadeService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(fidelidadeRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Ranking - deve retornar top 10 clientes")
    void ranking_deveRetornarTop10Clientes() {
        when(fidelidadeRepository.findTop10ByOrderByPontosAcumuladosDesc())
                .thenReturn(List.of(fidelidade));
        when(fidelidadeMapper.toFidelidadeResponseDTO(any())).thenReturn(fidelidadeResponse);

        var result = fidelidadeService.buscarRanking();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(150, result.get(0).pontosAcumulados());
        verify(fidelidadeRepository).findTop10ByOrderByPontosAcumuladosDesc();
    }

    @Test
    @DisplayName("Ranking - deve retornar lista vazia quando não há clientes")
    void ranking_deveRetornarListaVaziaQuandoNaoHaClientes() {
        when(fidelidadeRepository.findTop10ByOrderByPontosAcumuladosDesc())
                .thenReturn(List.of());

        var result = fidelidadeService.buscarRanking();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}