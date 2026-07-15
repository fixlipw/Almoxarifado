package com.ufc.almoxarifado.estoque.internal;

import com.ufc.almoxarifado.estoque.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

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

