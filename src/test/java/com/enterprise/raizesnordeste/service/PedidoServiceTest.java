package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.ItemPedidoRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.PedidoRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.*;
import com.enterprise.raizesnordeste.domain.enuns.CanalPedido;
import com.enterprise.raizesnordeste.domain.enuns.MeioPagamento;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import com.enterprise.raizesnordeste.domain.mapper.ItemPedidoMapper;
import com.enterprise.raizesnordeste.domain.mapper.PedidoMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private UnidadeRepository unidadeRepository;
    @Mock private ItemCardapioRepository itemCardapioRepository;
    @Mock private ItemPedidoRepository itemPedidoRepository;
    @Mock private FidelidadeRepository fidelidadeRepository;
    @Mock private EstoqueService estoqueService;
    @Mock private PagamentoMockService pagamentoMockService;
    @Mock private PedidoMapper pedidoMapper;
    @Mock private ItemPedidoMapper itemPedidoMapper;

    private Unidade unidade;
    private Cliente cliente;
    private Usuario usuario;
    private Item item;
    private ItemCardapio itemCardapio;
    private Pedido pedido;
    private Fidelidade fidelidade;
    private PedidoRequestDTO pedidoRequest;
    private PedidoResponseDTO pedidoResponse;

    @BeforeEach
    void setUp() {
        unidade = Unidade.builder()
                .id(1L)
                .nomeFantasia("Raízes Recife")
                .status(true)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("Carlos Silva")
                .email("carlos@email.com")
                .status(true)
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .statusConsentimento(true)
                .anonimizado(false)
                .build();

        item = Item.builder()
                .id(1L)
                .nome("Cuscuz")
                .preco(new BigDecimal("15.00"))
                .status(true)
                .build();

        itemCardapio = ItemCardapio.builder()
                .id(1L)
                .item(item)
                .disponivel(true)
                .ativo(true)
                .build();

        fidelidade = Fidelidade.builder()
                .id(1L)
                .cliente(cliente)
                .pontosAcumulados(0)
                .totalGasto(BigDecimal.ZERO)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .cliente(cliente)
                .unidade(unidade)
                .canal(CanalPedido.APP)
                .status(StatusPedido.CONFIRMADO)
                .meioPagamento(MeioPagamento.PIX)
                .valorTotal(new BigDecimal("15.00"))
                .build();

        pedidoResponse = new PedidoResponseDTO(
                1L, 1L, "Carlos Silva", 1L, "Raízes Recife",
                CanalPedido.APP, StatusPedido.CONFIRMADO,
                new BigDecimal("15.00"), MeioPagamento.PIX,
                List.of(), null, null, null, null
        );

        pedidoRequest = new PedidoRequestDTO(
                1L, 1L, CanalPedido.APP, MeioPagamento.PIX,
                List.of(new ItemPedidoRequestDTO(1L, 2)),
                false
        );
    }

    @Test
    @DisplayName("Listar - deve retornar página de pedidos")
    void listar_deveRetornarPaginaDePedidos() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(pedido));

        when(pedidoRepository.findAll(pageable)).thenReturn(page);
        when(pedidoMapper.toPedidoResponseDTO(any())).thenReturn(pedidoResponse);

        var result = pedidoService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(pedidoRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Buscar por ID - deve retornar pedido")
    void buscarPorId_deveRetornarPedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toPedidoResponseDTO(any())).thenReturn(pedidoResponse);

        var result = pedidoService.getPedidoById(1L);

        assertNotNull(result);
        verify(pedidoRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar por ID - deve lançar exceção para pedido não encontrado")
    void buscarPorId_deveLancarExcecaoParaPedidoNaoEncontrado() {
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.getPedidoById(99L));
    }

    @Test
    @DisplayName("Criar - deve criar pedido com sucesso sem cliente")
    void criar_deveCriarPedidoSemClienteComSucesso() {
        var requestSemCliente = new PedidoRequestDTO(
                null, 1L, CanalPedido.BALCAO, MeioPagamento.DINHEIRO,
                List.of(new ItemPedidoRequestDTO(1L, 1)),
                false
        );

        var authentication = mock(Authentication.class);
        var context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        when(authentication.getName()).thenReturn("atendente@email.com");

        var itemPedido = ItemPedido.builder()
                .id(1L)
                .itemCardapio(itemCardapio)
                .quantidade(1)
                .precoUnitario(new BigDecimal("15.00"))
                .build();

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(itemCardapioRepository.findByCardapioIdAndItemId(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemCardapio));
        when(pedidoMapper.toPedido(any(), any(), any())).thenReturn(pedido);
        when(itemPedidoMapper.toItemPedido(any(), any(), any())).thenReturn(itemPedido);
        when(pedidoRepository.save(any())).thenReturn(pedido);
        when(pedidoMapper.toPedidoResponseDTO(any())).thenReturn(pedidoResponse);
        doNothing().when(estoqueService).decrementarEstoquePorPedidoRealizado(anyLong(), anyLong(), anyInt());

        var result = pedidoService.createPedido(requestSemCliente);

        assertNotNull(result);
        verify(pedidoRepository).save(any());
        verify(pagamentoMockService).processarPagamento(any(), any());
    }

    @Test
    @DisplayName("Criar - deve lançar exceção para item indisponível")
    void criar_deveLancarExcecaoParaItemIndisponivel() {
        itemCardapio.setDisponivel(false);

        var authentication = mock(Authentication.class);
        var context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
        doReturn(java.util.Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("atendente@email.com");

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoMapper.toPedido(any(), any(), any())).thenReturn(pedido);
        when(itemCardapioRepository.findByCardapioIdAndItemId(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemCardapio));

        assertThrows(BusinessException.class,
                () -> pedidoService.createPedido(pedidoRequest));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar - deve lançar exceção para unidade não encontrada")
    void criar_deveLancarExcecaoParaUnidadeNaoEncontrada() {
        when(unidadeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.createPedido(pedidoRequest));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar - deve lançar exceção para pontos insuficientes")
    void criar_deveLancarExcecaoParaPontosInsuficientes() {
        var requestComPontos = new PedidoRequestDTO(
                1L, 1L, CanalPedido.APP, MeioPagamento.PIX,
                List.of(new ItemPedidoRequestDTO(1L, 1)),
                true
        );

        var authentication = mock(Authentication.class);
        var context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("carlos@email.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                .when(authentication).getAuthorities();

        var itemPedido = ItemPedido.builder()
                .id(1L)
                .itemCardapio(itemCardapio)
                .quantidade(1)
                .precoUnitario(new BigDecimal("15.00"))
                .build();

        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(clienteRepository.findByUsuarioEmail(anyString())).thenReturn(Optional.of(cliente));
        when(pedidoMapper.toPedido(any(), any(), any())).thenReturn(pedido);
        when(itemCardapioRepository.findByCardapioIdAndItemId(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemCardapio));
        when(itemPedidoMapper.toItemPedido(any(), any(), any())).thenReturn(itemPedido);
        when(fidelidadeRepository.findByClienteId(anyLong())).thenReturn(Optional.of(fidelidade));
        doNothing().when(estoqueService).decrementarEstoquePorPedidoRealizado(anyLong(), anyLong(), anyInt());

        assertThrows(BusinessException.class,
                () -> pedidoService.createPedido(requestComPontos));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Atualizar status - deve atualizar com sucesso")
    void atualizarStatus_deveAtualizarComSucesso() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenReturn(pedido);
        when(pedidoMapper.toPedidoResponseDTO(any())).thenReturn(pedidoResponse);

        var result = pedidoService.atualizarStatusPedido(1L, StatusPedido.EM_PREPARO);

        assertNotNull(result);
        verify(pedidoRepository).save(any());
    }

    @Test
    @DisplayName("Atualizar status - deve lançar exceção para pedido cancelado")
    void atualizarStatus_deveLancarExcecaoParaPedidoCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class,
                () -> pedidoService.atualizarStatusPedido(1L, StatusPedido.EM_PREPARO));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Atualizar status - deve lançar exceção para pedido finalizado")
    void atualizarStatus_deveLancarExcecaoParaPedidoFinalizado() {
        pedido.setStatus(StatusPedido.FINALIZADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class,
                () -> pedidoService.atualizarStatusPedido(1L, StatusPedido.EM_PREPARO));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelar - deve cancelar pedido com sucesso")
    void cancelar_deveCancelarComSucesso() {
        pedido.setItens(List.of());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenReturn(pedido);

        pedidoService.cancelarPedido(1L);

        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        verify(pedidoRepository).save(any());
    }

    @Test
    @DisplayName("Cancelar - deve lançar exceção para pedido já cancelado")
    void cancelar_deveLancarExcecaoParaPedidoJaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelar - deve lançar exceção para pedido finalizado")
    void cancelar_deveLancarExcecaoParaPedidoFinalizado() {
        pedido.setStatus(StatusPedido.FINALIZADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class,
                () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, never()).save(any());
    }
}