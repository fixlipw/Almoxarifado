package com.ufc.almoxarifado.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bloqueios")
public class HistoricoBloqueios {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_block_id", nullable = false)
    private Usuario administradorBloqueio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_unblock_id")
    private Usuario administradorDesbloqueio;

    @Column(columnDefinition = "TEXT")
    private String motivoDesbloqueio;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivoBloqueio;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataBloqueio;

    private LocalDateTime dataDesbloqueio;

}
