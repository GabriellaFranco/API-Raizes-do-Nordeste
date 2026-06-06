package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.EstoqueRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.MovimentacaoEstoqueRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.EstoqueResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.MovimentacaoEstoqueResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Estoque;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import com.enterprise.raizesnordeste.domain.mapper.EstoqueMapper;
import com.enterprise.raizesnordeste.domain.mapper.MovimentacaoEstoqueMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.EstoqueRepository;
import com.enterprise.raizesnordeste.repository.MovimentacaoEstoqueRepository;
import com.enterprise.raizesnordeste.repository.UnidadeRepository;
import com.enterprise.raizesnordeste.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueMapper estoqueMapper;
    private final MovimentacaoEstoqueMapper movimentacaoEstoqueMapper;

    public Page<EstoqueResponseDTO> getAllByUnidade(Long idUnidade, Pageable pageable) {
        buscarUnidade(idUnidade);
        return estoqueRepository.findAllByUnidadeId(idUnidade, pageable).map(estoqueMapper::toEstoqueResponseDTO);
    }

    public EstoqueResponseDTO getEstoqueById(Long id) {
        return estoqueMapper.toEstoqueResponseDTO(buscarEstoque(id));
    }

    public List<EstoqueResponseDTO> getAllAbaixoDoMinimo(Long idUnidade) {
        buscarUnidade(idUnidade);
        return estoqueRepository.findEstoqueCritico(idUnidade)
                .stream().map(estoqueMapper::toEstoqueResponseDTO).toList();
    }

    public boolean verificarDisponibilidade(Long idUnidade, Long idItem) {
        return estoqueRepository.findByUnidadeIdAndItemId(idUnidade, idItem).map(estoque
                -> estoque.getQuantidade() > 0).orElse(false);
    }

    @Transactional
    public MovimentacaoEstoqueResponseDTO registrarEntrada(MovimentacaoEstoqueRequestDTO request) {
        var estoque = buscarEstoque(request.idEstoque());
        var usuario = buscarUsuarioAutenticado();

        estoque.setQuantidade(estoque.getQuantidade() + request.quantidade());
        estoqueRepository.save(estoque);

        var movimentacao = movimentacaoEstoqueMapper.toMovimentacaoEstoque(request, estoque, usuario);
        movimentacaoEstoqueRepository.save(movimentacao);

        return movimentacaoEstoqueMapper.toMovimentacaoEstoqueResponseDTO(movimentacao);
    }

    @Transactional
    public EstoqueResponseDTO atualizaQuantidadeMinima(Long id, EstoqueRequestDTO request) {
        var estoque = buscarEstoque(id);
        estoque.setQuantidadeMinima(request.quantidadeMinima());
        estoqueRepository.save(estoque);
        return estoqueMapper.toEstoqueResponseDTO(estoque);
    }

    @Transactional
    void decrementarEstoquePorPedidoRealizado(Long idUnidade, Long idItem, Integer quantidade) {
        var estoque = estoqueRepository.findByUnidadeIdAndItemId(idUnidade, idItem)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para item: " + idItem));

        if (estoque.getQuantidade() < quantidade) {
            throw new BusinessException("Estoque insuficiente para o item: " + idItem);
        }

        estoque.setQuantidade(estoque.getQuantidade() - quantidade);
        estoqueRepository.save(estoque);
    }

    @Transactional
    void incrementarEstoquePorCancelamento(Long idUnidade, Long idItem, Integer quantidade) {
        var estoque = estoqueRepository.findByUnidadeIdAndItemId(idUnidade, idItem)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para item: " + idItem));

        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        estoqueRepository.save(estoque);
    }

    private Estoque buscarEstoque(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado: " + id));
    }

    private void buscarUnidade(Long id) {
        unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + id));
    }

    private Usuario buscarUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}