package com.xogame.game_service.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinGameRequest(

        @NotBlank(message = "L'identifiant du joueur est obligatoire")
        String playerId

) {
}