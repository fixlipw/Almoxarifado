package com.ufc.almoxarifado.estoque;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Integer quantidadeDisponivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEstoque tipo;

    private Boolean isAtivado = true;

}