package com.ufc.almoxarifado.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String matricula;

    @Column(nullable = false)
    private String senha;

    private String nome;

    private String sobrenome;

    private String curso;

    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleAcesso acesso;

    private Boolean isAtivada = false;

    private Boolean isBloqueado = false;

}