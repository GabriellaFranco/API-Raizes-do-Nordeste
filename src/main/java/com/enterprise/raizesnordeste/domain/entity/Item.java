package com.enterprise.raizesnordeste.domain.entity;

import com.enterprise.raizesnordeste.domain.enuns.CategoriaItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Builder
@Audited
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_item")
public class Item extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoriaItem categoria;

    @Column(nullable = false)
    @Builder.Default
    private Boolean status = true;
}
