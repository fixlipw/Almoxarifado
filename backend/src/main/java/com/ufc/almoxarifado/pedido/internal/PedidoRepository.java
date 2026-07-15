package com.ufc.almoxarifado.pedido.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true")
    List<Pedido> findApproved();

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true")
    Page<Pedido> findApprovedPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.solicitante.id = :userId")
    List<Pedido> findApprovedByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.solicitante.id = :userId")
    Page<Pedido> findApprovedByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NULL")
    List<Pedido> findPending();

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NULL AND p.solicitante.id = :userId")
    List<Pedido> findPendingByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NULL")
    Page<Pedido> findPendingPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NULL AND p.solicitante.id = :userId")
    Page<Pedido> findPendingByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false)")
    List<Pedido> findActive();

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND p.solicitante.id = :userId")
    List<Pedido> findActiveByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false)")
    Page<Pedido> findActivePaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND p.solicitante.id = :userId")
    Page<Pedido> findActiveByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.finalizado = true")
    List<Pedido> findReturned();

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.finalizado = true AND p.solicitante.id = :userId")
    List<Pedido> findReturnedByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.finalizado = true")
    Page<Pedido> findReturnedPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovado = true AND p.finalizado = true AND p.solicitante.id = :userId")
    Page<Pedido> findReturnedByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NOT NULL AND (p.aprovado IS NULL OR p.aprovado = false)")
    List<Pedido> findRejected();

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NOT NULL AND (p.aprovado IS NULL OR p.aprovado = false) AND p.solicitante.id = :userId")
    List<Pedido> findRejectedByUserId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NOT NULL AND (p.aprovado IS NULL OR p.aprovado = false)")
    Page<Pedido> findRejectedPaginated(Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.aprovador IS NOT NULL AND (p.aprovado IS NULL OR p.aprovado = false) AND p.solicitante.id = :userId")
    Page<Pedido> findRejectedByUserIdPaginated(Long userId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false)")
    Page<Pedido> findDelayedPaginated(@Param("threshold") LocalDateTime threshold, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND p.solicitante.id = :userId")
    Page<Pedido> findDelayedByUserIdPaginated(Long userId, @Param("threshold") LocalDateTime threshold, Pageable pageable);

    List<Pedido> findBySolicitanteId(Long userId);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false)")
    List<Pedido> findDelayed(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT p FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND p.solicitante.id = :userId")
    List<Pedido> findDelayedByUserId(Long userId, @Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countActive(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.aprovador IS NULL AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countPending(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.dataAprovacao IS NOT NULL AND p.dataAprovacao < :threshold AND p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countDelayed(@Param("threshold") LocalDateTime threshold, @Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.aprovado = true AND p.finalizado = true AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long countReturned(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(i.quantidadeItem), 0) FROM Pedido p JOIN p.itens i WHERE p.aprovado = true AND (p.finalizado IS NULL OR p.finalizado = false) AND (:userId IS NULL OR p.solicitante.id = :userId)")
    long sumBorrowedItems(@Param("userId") Long userId);
}
