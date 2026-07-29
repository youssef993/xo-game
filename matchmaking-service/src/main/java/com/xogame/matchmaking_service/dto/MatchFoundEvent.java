package com.xogame.matchmaking_service.dto;

import java.time.Instant;
import java.util.UUID;

public record MatchFoundEvent(

        String type,

        String playerId,

        String opponentId,

        UUID gameId,

        Instant occurredAt
) {
}
