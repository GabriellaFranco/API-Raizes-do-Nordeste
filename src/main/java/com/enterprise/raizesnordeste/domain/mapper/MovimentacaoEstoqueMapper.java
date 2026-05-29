package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.MovimentacaoEstoqueRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.MovimentacaoEstoqueResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Estoque;
import com.enterprise.raizesnordeste.domain.entity.MovimentacaoEstoque;
import com.enterprise.raizesnordeste.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class MovimentacaoEstoqueMapper {

    public MovimentacaoEstoque toMovimentacaoEstoque(MovimentacaoEstoqueRequestDTO dto, Estoque estoque, Usuario usuario) {
        return MovimentacaoEstoque.builder()
                .estoque(estoque)
                .usuario(usuario)
                .quantidade(dto.quantidade())
                .observacao(dto.observacao())
                .build();
    }

    public MovimentacaoEstoqueResponseDTO toResponse(MovimentacaoEstoque movimentacao) {
        return MovimentacaoEstoqueResponseDTO.builder()
                .id(movimentacao.getId())
                .idEstoque(movimentacao.getEstoque().getId())
                .nomeItem(movimentacao.getEstoque().getItem().getNome())
                .nomeFantasiaUnidade(movimentacao.getEstoque().getUnidade().getNomeFantasia())
                .quantidade(movimentacao.getQuantidade())
                .observacao(movimentacao.getObservacao())
                .responsavel(movimentacao.getCreatedBy())
                .createdAt(movimentacao.getCreatedAt())
                .build();
    }
}
