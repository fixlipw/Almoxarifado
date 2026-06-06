package com.ufc.almoxarifado.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_social")
    private String nomeSocial;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    @Column(nullable = false)
    @JsonProperty("is_ativo")
    private Boolean status;

    @JsonProperty("is_bloqueado")
    private Boolean bloqueado;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private Curso curso;

    @Column(unique = true, updatable = false)
    private String matricula;

    private String fotoPerfil;

}