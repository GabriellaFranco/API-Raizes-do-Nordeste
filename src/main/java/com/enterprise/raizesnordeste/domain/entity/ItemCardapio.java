package com.enterprise.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import jakarta.persistence.Id;

@Builder
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_item_cardapio")
public class ItemCardapio extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cardapio", nullable = false)
    private Cardapio cardapio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item", nullable = false)
    private Item item;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponivel = true;
}
