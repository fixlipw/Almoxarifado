package com.ufc.almoxarifado.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, updatable = false)
    private String codigoPedido;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovador_id")
    private Usuario aprovador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finalizador_id")
    private Usuario finalizador;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataPedido;

    private LocalDateTime dataFinalizado;

    private Boolean aprovado = false;

    private Boolean finalizado = false;

    private Boolean emprestimoEspecial = false;

    private String hash;

}