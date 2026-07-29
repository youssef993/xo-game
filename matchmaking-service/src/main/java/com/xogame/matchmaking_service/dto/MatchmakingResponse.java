package com.xogame.matchmaking_service.dto;

import com.xogame.matchmaking_service.domain.MatchmakingStatus;

import java.time.Instant;
import java.util.UUID;

public record MatchmakingResponse(

        UUID ticketId,

        MatchmakingStatus status,

        String playerId,

        String opponentId,

        UUID gameId,

        Instant searchingSince,

        Instant matchedAt
) {
}