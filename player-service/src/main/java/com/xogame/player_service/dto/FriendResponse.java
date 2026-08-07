package com.xogame.player_service.dto;

import com.xogame.player_service.domain.PlayerStatus;

import java.util.UUID;

public record FriendResponse(
        UUID id,
        String username,
        String avatarUrl,
        PlayerStatus status,
        int wins,
        int losses,
        int draws
) {
}
