package com.xogame.game_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMatchedGameRequest(

        @NotBlank
        String playerXId,

        @NotBlank
        String playerOId
) {
}