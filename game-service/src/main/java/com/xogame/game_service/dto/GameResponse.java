package com.xogame.game_service.dto;

import com.xogame.game_service.domain.enums.GameStatus;
import com.xogame.game_service.domain.enums.PlayerSymbol;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameResponse(
        UUID id,
        String playerXId,
        String playerOId,
        List<PlayerSymbol> board,
        PlayerSymbol currentTurn,
        GameStatus status,
        String winnerId,
        Long version,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        List<MoveResponse> moves
) {
}
