package com.xogame.game_service.domain.enums;

public enum GameStatus {

    WAITING_FOR_PLAYER,
    IN_PROGRESS,
    X_WON,
    O_WON,
    DRAW,
    ABANDONED;

    public boolean isFinished() {
        return this == X_WON
                || this == O_WON
                || this == DRAW
                || this == ABANDONED;
    }
}
