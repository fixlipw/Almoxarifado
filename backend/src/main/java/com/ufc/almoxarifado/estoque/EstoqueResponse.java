package com.ufc.almoxarifado.estoque;

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

