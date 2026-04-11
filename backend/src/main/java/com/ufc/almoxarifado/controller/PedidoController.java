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
    public ResponseEntity<List<PedidoResponse>> findAll(@RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(pedidoService.findAll(userId));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<PedidoResponse>> findApproved(@RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(pedidoService.findApproved(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PedidoResponse>> findPending(@RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(pedidoService.findPending(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PedidoResponse>> findActive(@RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(pedidoService.findActive(userId));
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<PedidoResponse>> findRejected(@RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(pedidoService.findRejected(userId));
    }

    @GetMapping("/delayed")
    public ResponseEntity<List<PedidoResponse>> findDelayed(@RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(pedidoService.findDelayed(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(@PathVariable UUID id, @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.update(id, request));
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<PedidoResponse> approvePedido(@PathVariable UUID id, @RequestParam UUID aprovadorId) {
        return ResponseEntity.ok(pedidoService.approvePedido(id, aprovadorId));
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<PedidoResponse> rejectPedido(@PathVariable UUID id, @RequestParam UUID finalizadorId) {
        return ResponseEntity.ok(pedidoService.rejectPedido(id, finalizadorId));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<PedidoResponse> returnPedido(@PathVariable UUID id, @RequestParam UUID finalizadorId) {
        return ResponseEntity.ok(pedidoService.returnPedido(id, finalizadorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
}
