package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.EstoqueRequest;
import com.ufc.almoxarifado.dto.EstoqueResponse;
import com.ufc.almoxarifado.entity.Estoque;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueResponse create(EstoqueRequest request) {
        Estoque entity = new Estoque();
        applyRequest(entity, request);
        return toResponse(estoqueRepository.save(entity));
    }

    public List<EstoqueResponse> findAll() {
        return estoqueRepository.findAll().stream().map(this::toResponse).toList();
    }

    public EstoqueResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public EstoqueResponse update(UUID id, EstoqueRequest request) {
        Estoque entity = getEntity(id);
        applyRequest(entity, request);
        return toResponse(estoqueRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estoque nao encontrado para id: " + id);
        }
        estoqueRepository.deleteById(id);
    }

    public Estoque getEntity(UUID id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque nao encontrado para id: " + id));
    }

    private void applyRequest(Estoque entity, EstoqueRequest request) {
        entity.setNome(request.nome());
        entity.setQuantidade(request.quantidade());
        entity.setTipo(request.tipo());
        if (request.isAtivado() != null) {
            entity.setIsAtivado(request.isAtivado());
        }
    }

    private EstoqueResponse toResponse(Estoque entity) {
        return new EstoqueResponse(
                entity.getId(),
                entity.getNome(),
                entity.getQuantidade(),
                entity.getTipo(),
                entity.getIsAtivado()
        );
    }
}


