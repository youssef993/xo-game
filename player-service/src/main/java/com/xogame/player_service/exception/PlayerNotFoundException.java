package com.xogame.player_service.exception;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String message){
        super(message);
    }
}
