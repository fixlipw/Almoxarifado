package com.ufc.almoxarifado.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Papel {

    ADMIN("Administrador"),
    ALUNO("Aluno"),
    BOLSISTA("Bolsista");

    private final String descricao;

    @JsonCreator
    public static Papel fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(papel ->
                        papel.name().equalsIgnoreCase(value)
                                || papel.descricao.equalsIgnoreCase(value)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Papel inválido: " + value));
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}
