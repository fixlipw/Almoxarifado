package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.ItemPedidoRequest;
import com.ufc.almoxarifado.dto.ItemPedidoResponse;
import com.ufc.almoxarifado.service.ItemPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/itens-pedido")
@RequiredArgsConstructor
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    @PostMapping
    public ResponseEntity<ItemPedidoResponse> create(@Valid @RequestBody ItemPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemPedidoService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ItemPedidoResponse>> findAll() {
        return ResponseEntity.ok(itemPedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedidoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(itemPedidoService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedidoResponse> update(@PathVariable UUID id, @Valid @RequestBody ItemPedidoRequest request) {
        return ResponseEntity.ok(itemPedidoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        itemPedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

