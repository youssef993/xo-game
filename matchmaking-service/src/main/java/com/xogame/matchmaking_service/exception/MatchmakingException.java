package com.xogame.matchmaking_service.exception;

public class MatchmakingException
        extends RuntimeException {

    public MatchmakingException(
            String message
    ) {
        super(message);
    }
}