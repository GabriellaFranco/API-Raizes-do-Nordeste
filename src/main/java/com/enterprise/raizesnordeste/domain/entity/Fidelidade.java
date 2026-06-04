package com.enterprise.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Builder
@Audited
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_fidelidade")
public class Fidelidade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    @Column(name = "pontos_acumulados", nullable = false)
    @Builder.Default
    private Integer pontosAcumulados = 0;

    @Column(name = "total_gasto", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalGasto = BigDecimal.ZERO;
}
