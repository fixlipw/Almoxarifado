package com.ufc.almoxarifado.dto.response;

import com.ufc.almoxarifado.model.TipoEstoque;

import java.util.UUID;

public record EstoqueResponse(
        UUID id,
        String nome,
        Integer quantidade,
        Integer quantidadeDisponivel,
        TipoEstoque tipo,
        Boolean isAtivado
) {
}
