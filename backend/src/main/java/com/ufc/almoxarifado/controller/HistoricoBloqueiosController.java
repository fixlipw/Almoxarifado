package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.HistoricoBloqueiosRequest;
import com.ufc.almoxarifado.dto.HistoricoBloqueiosResponse;
import com.ufc.almoxarifado.service.AuthSecurityService;
import com.ufc.almoxarifado.service.HistoricoBloqueiosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/historico-bloqueios")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class HistoricoBloqueiosController {

    private final HistoricoBloqueiosService historicoBloqueiosService;
    private final AuthSecurityService authSecurityService;

    @PostMapping
    public ResponseEntity<HistoricoBloqueiosResponse> create(
            @Valid @RequestBody HistoricoBloqueiosRequest request,
            Authentication authentication) {
        Long current = authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(historicoBloqueiosService.create(request, current));
    }

    @GetMapping
    public ResponseEntity<List<HistoricoBloqueiosResponse>> findAll() {
        return ResponseEntity.ok(historicoBloqueiosService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoBloqueiosResponse> findById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(historicoBloqueiosService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoricoBloqueiosResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody HistoricoBloqueiosRequest request,
            Authentication authentication) {
        Long current = authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.ok(historicoBloqueiosService.update(id, request, current));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        historicoBloqueiosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
