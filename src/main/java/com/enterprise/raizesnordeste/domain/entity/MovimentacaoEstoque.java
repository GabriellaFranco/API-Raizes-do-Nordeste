package com.enterprise.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Builder
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_movimentacao_estoque")
public class MovimentacaoEstoque extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estoque", nullable = false)
    private Estoque estoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer quantidade;

    @Column
    private String observacao;
}
