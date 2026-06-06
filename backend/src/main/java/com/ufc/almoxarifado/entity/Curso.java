package com.ufc.almoxarifado.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Curso {

    IA("Inteligência Artificial"),
    CC("Ciência da Computação"),
    SI("Sistemas de Informação"),
    RC("Redes de Computadores"),
    ES("Engenharia de Software"),
    EC("Engenharia de Computação"),
    DD("Design Digital");

    private final String descricao;

    @JsonCreator
    public static Curso fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(curso ->
                        curso.name().equalsIgnoreCase(value)
                                || curso.descricao.equalsIgnoreCase(value)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Curso inválido: " + value));
    }

}
