package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.EstoqueRequest;
import com.ufc.almoxarifado.dto.EstoqueResponse;
import com.ufc.almoxarifado.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping
    public ResponseEntity<EstoqueResponse> create(@Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<EstoqueResponse>> findAll() {
        return ResponseEntity.ok(estoqueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(estoqueService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponse> update(@PathVariable UUID id, @Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.ok(estoqueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        estoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

