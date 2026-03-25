package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
}

