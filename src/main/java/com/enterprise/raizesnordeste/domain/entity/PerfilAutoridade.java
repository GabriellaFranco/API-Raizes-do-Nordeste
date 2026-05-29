package com.enterprise.raizesnordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Builder
@Audited
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "perfil_autoridade")
public class PerfilAutoridade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(length = 200)
    private String descricao;

    @Builder.Default
    boolean status = true;
}
