package com.ufc.almoxarifado.mapper;

import com.ufc.almoxarifado.model.Pedido;

import com.ufc.almoxarifado.dto.response.ItemPedidoResponse;
import com.ufc.almoxarifado.dto.response.PedidoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PedidoMapper {

    private final UsuarioMapper usuarioMapper;

    public PedidoResponse toResponse(Pedido entity) {
        if (entity == null) {
            return null;
        }

        List<ItemPedidoResponse> itens = entity.getItens().stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        entity.getId(),
                        item.getEstoque().getId(),
                        item.getEstoque().getNome(),
                        item.getQuantidadeItem()
                ))
                .toList();

        return new PedidoResponse(
                entity.getId(),
                entity.getCodigoPedido(),
                entity.getFeedback(),
                usuarioMapper.toResponse(entity.getSolicitante()),
                usuarioMapper.toResponse(entity.getAprovador()),
                usuarioMapper.toResponse(entity.getFinalizador()),
                itens,
                entity.getDataSolicitacao(),
                entity.getDataAprovacao(),
                entity.getDataFinalizado(),
                entity.getDataAtualizacao(),
                entity.getStatus(),
                entity.getEmprestimoEspecial(),
                entity.getHash()
        );
    }
}
