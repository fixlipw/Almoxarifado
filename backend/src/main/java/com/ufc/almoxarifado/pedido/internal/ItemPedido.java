package com.ufc.almoxarifado.pedido.internal;

import com.ufc.almoxarifado.estoque.Estoque;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false)
    private Estoque estoque;

    @Column(nullable = false)
    private Integer quantidadeItem;

}