package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.response.EstoqueResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Estoque;
import com.enterprise.raizesnordeste.domain.entity.Item;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.EstoqueMapper;
import com.enterprise.raizesnordeste.domain.mapper.MovimentacaoEstoqueMapper;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @InjectMocks
    private EstoqueService estoqueService;

    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private UnidadeRepository unidadeRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EstoqueMapper estoqueMapper;
    @Mock
    private MovimentacaoEstoqueMapper movimentacaoEstoqueMapper;

    private Unidade unidade;
    private Item item;
    private Estoque estoque;
    private Usuario usuario;
    private EstoqueResponseDTO estoqueResponse;

    @BeforeEach
    void setUp() {
        unidade = Unidade.builder()
                .id(1L)
                .nomeFantasia("Raízes Recife")
                .status(true)
                .build();

        item = Item.builder()
                .id(1L)
                .nome("Cuscuz")
                .status(true)
                .build();

        estoque = Estoque.builder()
                .id(1L)
                .unidade(unidade)
                .item(item)
                .quantidade(10)
                .quantidadeMinima(5)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("Gerente")
                .email("gerente@email.com")
                .status(true)
                .build();

        estoqueResponse = EstoqueResponseDTO.builder()
                .id(1L)
                .idItem(1L)
                .nomeItem("Cuscuz")
                .idUnidade(1L)
                .nomeFantasiaUnidade("Raízes Recife")
                .quantidade(10)
                .quantidadeMinima(5)
                .abaixoDoMinimo(false)
                .build();
    }

    @Test
    @DisplayName("Listar por unidade - deve retornar página de estoque")
    void listarPorUnidade_deveRetornarPagina() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(estoque));

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(estoqueRepository.findAllByUnidadeId(1L, pageable)).thenReturn(page);
        when(estoqueMapper.toEstoqueResponseDTO(any())).thenReturn(estoqueResponse);

        var result = estoqueService.getAllByUnidade(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(estoqueRepository).findAllByUnidadeId(1L, pageable);
    }

    @Test
    @DisplayName("Listar por unidade - deve lançar exceção para unidade não encontrada")
    void listarPorUnidade_deveLancarExcecaoParaUnidadeNaoEncontrada() {
        when(unidadeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> estoqueService.getAllByUnidade(99L, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("Buscar por ID - deve retornar estoque")
    void buscarPorId_deveRetornarEstoque() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.toEstoqueResponseDTO(any())).thenReturn(estoqueResponse);

        var result = estoqueService.getEstoqueById(1L);

        assertNotNull(result);
        verify(estoqueRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar por ID - deve lançar exceção para estoque não encontrado")
    void buscarPorId_deveLancarExcecaoParaEstoqueNaoEncontrado() {
        when(estoqueRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> estoqueService.getEstoqueById(99L));
    }

    @Test
    @DisplayName("Listar abaixo do mínimo - deve retornar itens críticos")
    void listarAbaixoDoMinimo_deveRetornarItensCriticos() {
        estoque.setQuantidade(0);
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(estoqueRepository.findEstoqueCritico(1L)).thenReturn(List.of(estoque));
        when(estoqueMapper.toEstoqueResponseDTO(any())).thenReturn(estoqueResponse);

        var result = estoqueService.getAllAbaixoDoMinimo(1L);

        assertNotNull(result);
    }
}