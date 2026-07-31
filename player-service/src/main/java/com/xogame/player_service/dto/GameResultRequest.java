package com.xogame.player_service.dto;

import jakarta.validation.constraints.NotBlank;

public record GameResultRequest(
        @NotBlank
        String playerXId,
        @NotBlank
        String playerOId,
        @NotBlank
        GameResult result
) {
}
