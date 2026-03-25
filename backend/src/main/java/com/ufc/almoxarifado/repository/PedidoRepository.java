package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
}

