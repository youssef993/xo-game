package com.xogame.player_service.dto;

import java.util.UUID;

public record FriendshipRequest(
        String recieverId,
        String senderId
) {
}
