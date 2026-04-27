package com.ufc.almoxarifado.dto;

import com.ufc.almoxarifado.entity.TipoEstoque;

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

