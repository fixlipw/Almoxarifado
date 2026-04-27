package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.Estoque;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

    @Query("SELECT e FROM Estoque e")
    Page<Estoque> findAllPaginated(@NonNull Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
                    FROM Pedido p
                    JOIN p.itens i
                    WHERE i.estoque.id = :estoqueId
                      AND p.aprovado = true
                      AND (p.finalizado IS NULL OR p.finalizado = false)
            """)
    boolean existsActiveOrdersByEstoqueId(UUID id);
}

