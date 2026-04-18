package com.ufc.almoxarifado.dto;

import java.util.List;

public record SigaaResponse(
        Boolean error,
        String login,
        String nome,
        String foto,
        String matricula,
        String curso,
        String nivel,
        String status,
        String entrada,
        String semestre,
        List<SigaaCadeiraResponse> cadeiras
) {
}

