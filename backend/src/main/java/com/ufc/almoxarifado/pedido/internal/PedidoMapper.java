package com.ufc.almoxarifado.pedido.internal;

import com.ufc.almoxarifado.pedido.ItemPedidoResponse;
import com.ufc.almoxarifado.pedido.PedidoResponse;
import com.ufc.almoxarifado.usuario.UsuarioMapper;
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
                entity.getAprovado(),
                entity.getFinalizado(),
                entity.getEmprestimoEspecial(),
                entity.getHash()
        );
    }
}
