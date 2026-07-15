package com.ufc.almoxarifado.estoque;

import com.ufc.almoxarifado.estoque.internal.EstoqueMapper;
import com.ufc.almoxarifado.estoque.internal.EstoqueRepository;
import com.ufc.almoxarifado.exception.InsufficientStockException;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    public EstoqueResponse create(EstoqueRequest request) {
        Estoque entity = new Estoque();
        estoqueMapper.applyRequest(entity, request);
        return estoqueMapper.toResponse(estoqueRepository.save(entity));
    }

    public List<EstoqueResponse> findAll() {
        return estoqueRepository.findAll().stream().map(estoqueMapper::toResponse).toList();
    }

    public Page<EstoqueResponse> findAllPaginated(int page, int size) {
        return estoqueRepository.findAll(PageRequest.of(page, size).withSort(Sort.by("nome").ascending()))
                .map(estoqueMapper::toResponse);
    }

    public EstoqueResponse findById(UUID id) {
        return estoqueMapper.toResponse(getEntity(id));
    }

    public EstoqueResponse update(UUID id, EstoqueRequest request) {
        Estoque entity = getEntity(id);

        if (Boolean.FALSE.equals(request.isAtivado()) && Boolean.TRUE.equals(entity.getIsAtivado())) {
            boolean hasActiveOrders = estoqueRepository.existsActiveOrdersByEstoqueId(id);
            if (hasActiveOrders) {
                throw new IllegalStateException("não é possível atualizar este item, pois ele está associado a pedidos ativos.");
            }
        }

        estoqueMapper.applyRequest(entity, request);
        return estoqueMapper.toResponse(estoqueRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estoque não encontrado para id: " + id);
        }
        estoqueRepository.deleteById(id);
    }

    public Estoque getEntity(UUID id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para id: " + id));
    }

    @Transactional
    public void deductStock(UUID id, int quantity) {
        Estoque estoque = getEntity(id);
        if (estoque.getQuantidadeDisponivel() < quantity) {
            throw new InsufficientStockException("Estoque insuficiente para o item: " + estoque.getNome());
        }
        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - quantity);
        estoqueRepository.save(estoque);
    }

    @Transactional
    public void replenishStock(UUID id, int quantity) {
        Estoque estoque = getEntity(id);
        estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + quantity);
        estoqueRepository.save(estoque);
    }
}
