package com.xogame.game_service.dto;

import com.xogame.game_service.domain.enums.PlayerSymbol;

import java.time.Instant;
import java.util.UUID;

public record MoveResponse(
        UUID id,
        String playerId,
        PlayerSymbol symbol,
        Integer cellIndex,
        Integer moveNumber,
        Instant playedAt
) {
}