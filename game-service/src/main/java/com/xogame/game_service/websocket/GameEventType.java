package com.xogame.game_service.websocket;

public final class GameEventType {

    public static final String GAME_CREATED = "GAME_CREATED";
    public static final String PLAYER_JOINED = "PLAYER_JOINED";
    public static final String MOVE_PLAYED = "MOVE_PLAYED";
    public static final String GAME_FINISHED = "GAME_FINISHED";
    public static final String GAME_ABANDONED = "GAME_ABANDONED";

    private GameEventType() {
    }
}
