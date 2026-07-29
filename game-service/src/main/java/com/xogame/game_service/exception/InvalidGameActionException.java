package com.xogame.game_service.exception;

public class InvalidGameActionException extends RuntimeException {

    public InvalidGameActionException(String message) {
        super(message);
    }
}