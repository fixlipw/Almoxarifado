package com.ufc.almoxarifado.historico.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoricoBloqueiosRepository extends JpaRepository<HistoricoBloqueios, UUID> {
}

