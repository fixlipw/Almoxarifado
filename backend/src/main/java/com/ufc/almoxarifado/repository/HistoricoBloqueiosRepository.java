package com.ufc.almoxarifado.repository;

import com.ufc.almoxarifado.model.HistoricoBloqueios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoricoBloqueiosRepository extends JpaRepository<HistoricoBloqueios, UUID> {
}
