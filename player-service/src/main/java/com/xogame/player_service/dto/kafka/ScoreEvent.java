package com.xogame.player_service.dto.kafka;

import jakarta.validation.constraints.NotBlank;

public record ScoreEvent(
        @NotBlank String playerXId,
        @NotBlank String playerOId,
        String winnerId
) {
}
