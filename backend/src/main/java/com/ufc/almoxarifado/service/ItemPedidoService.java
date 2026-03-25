package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.ItemPedidoRequest;
import com.ufc.almoxarifado.dto.ItemPedidoResponse;
import com.ufc.almoxarifado.entity.Estoque;
import com.ufc.almoxarifado.entity.ItemPedido;
import com.ufc.almoxarifado.entity.Pedido;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.EstoqueRepository;
import com.ufc.almoxarifado.repository.ItemPedidoRepository;
import com.ufc.almoxarifado.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final EstoqueRepository estoqueRepository;

    public ItemPedidoResponse create(ItemPedidoRequest request) {
        ItemPedido entity = new ItemPedido();
        applyRequest(entity, request);
        return toResponse(itemPedidoRepository.save(entity));
    }

    public List<ItemPedidoResponse> findAll() {
        return itemPedidoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ItemPedidoResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public ItemPedidoResponse update(UUID id, ItemPedidoRequest request) {
        ItemPedido entity = getEntity(id);
        applyRequest(entity, request);
        return toResponse(itemPedidoRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!itemPedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("ItemPedido nao encontrado para id: " + id);
        }
        itemPedidoRepository.deleteById(id);
    }

    private ItemPedido getEntity(UUID id) {
        return itemPedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemPedido nao encontrado para id: " + id));
    }

    private void applyRequest(ItemPedido entity, ItemPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado para id: " + request.pedidoId()));

        Estoque estoque = estoqueRepository.findById(request.estoqueId())
                .orElseThrow(() -> new ResourceNotFoundException("Estoque nao encontrado para id: " + request.estoqueId()));

        entity.setPedido(pedido);
        entity.setEstoque(estoque);
        entity.setQuantidadeItem(request.quantidadeItem());
    }

    private ItemPedidoResponse toResponse(ItemPedido entity) {
        return new ItemPedidoResponse(
                entity.getId(),
                entity.getPedido() != null ? entity.getPedido().getId() : null,
                entity.getEstoque() != null ? entity.getEstoque().getId() : null,
                entity.getQuantidadeItem()
        );
    }
}

