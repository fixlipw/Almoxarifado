package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true")
    List<Pedido> findApproved();

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.solicitante.id = :userId")
    List<Pedido> findApprovedByUserId(UUID userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NULL")
    List<Pedido> findPending();

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NULL AND p.solicitante.id = :userId")
    List<Pedido> findPendingByUserId(UUID userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false)")
    List<Pedido> findActive();

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND p.solicitante.id = :userId")
    List<Pedido> findActiveByUserId(UUID userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NOT NULL AND (p.aprovado IS NULL OR p.aprovado = false)")
    List<Pedido> findRejected();

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NOT NULL AND (p.aprovado IS NULL OR p.aprovado = false) AND p.solicitante.id = :userId")
    List<Pedido> findRejectedByUserId(UUID userId);

    List<Pedido> findBySolicitanteId(UUID userId);
}
