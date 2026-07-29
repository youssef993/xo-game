package com.xogame.game_service.websocket;

import com.xogame.game_service.dto.GameResponse;

public record GameStateChangedEvent(
        String eventType,
        GameResponse game
) {
}
