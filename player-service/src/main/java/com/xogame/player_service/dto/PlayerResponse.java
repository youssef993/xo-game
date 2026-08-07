package com.xogame.player_service.dto;

import com.xogame.player_service.domain.PlayerStatus;

import java.time.Instant;
import java.util.UUID;

public record PlayerResponse(
        UUID id,
        String keycloakId,
        String username,
        String email,
        String avatarUrl,
        PlayerStatus status,
        int gamesPlayed,
        int wins,
        int losses,
        int draws,
        long score,
        long meilleurSerie,
        long serieActuelle,
        double taux,
        Instant createdAt
) {
}
