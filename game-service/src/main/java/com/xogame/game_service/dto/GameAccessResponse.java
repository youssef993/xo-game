package com.xogame.game_service.dto;

import java.util.UUID;

public record GameAccessResponse(
        UUID gameId,
        String playerId,
        boolean allowed
) {
}