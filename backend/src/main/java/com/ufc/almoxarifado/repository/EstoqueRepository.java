package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {
}

