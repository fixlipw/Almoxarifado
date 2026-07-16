package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO")
    List<Pedido> findApproved();

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO")
    Page<Pedido> findApprovedPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO AND p.solicitante.id = :userId")
    List<Pedido> findApprovedByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO AND p.solicitante.id = :userId")
    Page<Pedido> findApprovedByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.PENDENTE")
    List<Pedido> findPending();

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.PENDENTE AND p.solicitante.id = :userId")
    List<Pedido> findPendingByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.PENDENTE")
    Page<Pedido> findPendingPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.PENDENTE AND p.solicitante.id = :userId")
    Page<Pedido> findPendingByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO")
    List<Pedido> findActive();

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO AND p.solicitante.id = :userId")
    List<Pedido> findActiveByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO")
    Page<Pedido> findActivePaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.APROVADO AND p.solicitante.id = :userId")
    Page<Pedido> findActiveByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.FINALIZADO")
    List<Pedido> findReturned();

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.FINALIZADO AND p.solicitante.id = :userId")
    List<Pedido> findReturnedByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.FINALIZADO")
    Page<Pedido> findReturnedPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.FINALIZADO AND p.solicitante.id = :userId")
    Page<Pedido> findReturnedByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.REJEITADO")
    List<Pedido> findRejected();

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.REJEITADO AND p.solicitante.id = :userId")
    List<Pedido> findRejectedByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.REJEITADO")
    Page<Pedido> findRejectedPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status = StatusPedido.REJEITADO AND p.solicitante.id = :userId")
    Page<Pedido> findRejectedByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.status = StatusPedido.APROVADO")
    Page<Pedido> findDelayedPaginated(@Param("threshold") LocalDateTime threshold, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.status = StatusPedido.APROVADO AND p.solicitante.id = :userId")
    Page<Pedido> findDelayedByUserIdPaginated(Long userId, @Param("threshold") LocalDateTime threshold, Pageable pageable);

    List<Pedido> findBySolicitanteId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.status = StatusPedido.APROVADO")
    List<Pedido> findDelayed(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.status = StatusPedido.APROVADO AND p.solicitante.id = :userId")
    List<Pedido> findDelayedByUserId(Long userId, @Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = StatusPedido.APROVADO AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countActive(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = StatusPedido.PENDENTE AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countPending(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.status = StatusPedido.APROVADO AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countDelayed(@Param("threshold") LocalDateTime threshold, @Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = StatusPedido.FINALIZADO AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countReturned(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(i.quantidadeItem), 0) FROM Pedido p JOIN p.itens i WHERE p.status = StatusPedido.APROVADO AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long sumBorrowedItems(@Param("userId") Long userId);
}
