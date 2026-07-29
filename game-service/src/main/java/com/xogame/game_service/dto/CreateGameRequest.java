package com.xogame.game_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGameRequest(

        @NotBlank(message = "Le créateur de la partie est obligatoire")
        String creatorId

) {
}