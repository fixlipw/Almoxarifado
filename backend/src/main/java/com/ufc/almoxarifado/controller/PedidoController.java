package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.PedidoRequest;
import com.ufc.almoxarifado.dto.PedidoResponse;
import com.ufc.almoxarifado.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> create(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(@PathVariable UUID id, @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

