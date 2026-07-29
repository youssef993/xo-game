package com.xogame.game_service.websocket;

import com.xogame.game_service.dto.GameResponse;

import java.time.Instant;
import java.util.UUID;

public record GameUpdatedEvent(
        String type,
        UUID gameId,
        GameResponse game,
        Instant occurredAt
) {
}
