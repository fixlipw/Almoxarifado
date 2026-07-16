package com.ufc.almoxarifado.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record HistoricoBloqueiosRequest(
        @NotNull(message = "Usuário é obrigatório") Long usuarioId,
        @Size(max = 2000, message = "Motivo do desbloqueio deve ter no máximo 2000 caracteres") String motivoDesbloqueio,
        @NotBlank(message = "Motivo do bloqueio é obrigatório")
        @Size(max = 2000, message = "Motivo do bloqueio deve ter no máximo 2000 caracteres") String motivoBloqueio,
        @PastOrPresent(message = "Data do bloqueio não pode ser futura") LocalDateTime dataBloqueio,
        @PastOrPresent(message = "Data do desbloqueio não pode ser futura") LocalDateTime dataDesbloqueio
) {
}
