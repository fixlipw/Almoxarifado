package com.ufc.almoxarifado.estoque;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private static final MediaType APPLICATION_PDF = MediaType.parseMediaType("application/pdf");

    private final EstoqueService estoqueService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<EstoqueResponse> create(
            @Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<EstoqueResponse>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(estoqueService.findAllPaginated(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponse> findById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(estoqueService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<EstoqueResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.ok(estoqueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        estoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
