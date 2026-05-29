package com.enterprise.raizesnordeste.domain.entity;

import com.enterprise.raizesnordeste.domain.enuns.Estado;
import com.enterprise.raizesnordeste.domain.enuns.Regiao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "tb_unidade")
public class Unidade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia", nullable = false, length = 100)
    private String nomeFantasia;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(nullable = false, length = 100)
    private String contato;

    @NotNull(message = "Estado é obrigatório")
    Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Regiao regiao;

    @Column(nullable = false)
    @Builder.Default
    private Boolean status = true;
}
