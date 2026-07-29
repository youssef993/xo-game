package com.xogame.game_service.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(UUID gameId) {
        super("Partie introuvable : " + gameId);
    }
}
