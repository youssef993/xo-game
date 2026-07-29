package com.xogame.game_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlayMoveRequest(

        @NotNull(message = "La case est obligatoire")
        @Min(value = 0, message = "La case minimale est 0")
        @Max(value = 8, message = "La case maximale est 8")
        Integer cellIndex

) {
}