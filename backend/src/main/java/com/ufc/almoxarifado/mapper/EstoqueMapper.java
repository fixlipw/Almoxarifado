package com.ufc.almoxarifado.mapper;

import com.ufc.almoxarifado.model.Estoque;
import com.ufc.almoxarifado.dto.request.EstoqueRequest;
import com.ufc.almoxarifado.dto.response.EstoqueResponse;
import org.springframework.stereotype.Component;

@Component
public class EstoqueMapper {

    public EstoqueResponse toResponse(Estoque entity) {
        if (entity == null) {
            return null;
        }
        return new EstoqueResponse(
                entity.getId(),
                entity.getNome(),
                entity.getQuantidade(),
                entity.getQuantidadeDisponivel(),
                entity.getTipo(),
                entity.getIsAtivado()
        );
    }

    public void applyRequest(Estoque entity, EstoqueRequest request) {
        if (request == null || entity == null) {
            return;
        }
        entity.setNome(request.nome());
        entity.setQuantidadeDisponivel(request.quantidadeDisponivel());
        entity.setQuantidade(request.quantidade());
        entity.setTipo(request.tipo());
        if (request.isAtivado() != null) {
            entity.setIsAtivado(request.isAtivado());
        }
    }
}
