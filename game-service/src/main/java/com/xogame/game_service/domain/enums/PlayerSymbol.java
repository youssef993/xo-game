package com.xogame.game_service.domain.enums;

public enum PlayerSymbol {

    X,
    O;

    public PlayerSymbol opposite() {
        return this == X ? O : X;
    }
}
