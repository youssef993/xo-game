package com.xogame.player_service.dto;

import jakarta.validation.constraints.Size;

public record UpdatePlayerRequest(
        @Size(min = 3, max = 50)
        String username,

        @Size(max = 500)
        String avatarUrl
) {
}
