package com.enterprise.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

@Builder
@Audited
@Getter @Setter
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_perfil_autoridade")
public class PerfilAutoridade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(length = 200)
    private String descricao;

    @Builder.Default
    private boolean status = true;
}
