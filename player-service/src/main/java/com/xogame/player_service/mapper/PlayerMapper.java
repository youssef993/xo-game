package com.xogame.player_service.mapper;

import com.xogame.player_service.domain.Player;
import com.xogame.player_service.dto.PlayerResponse;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getKeycloakId(),
                player.getUsername(),
                player.getEmail(),
                player.getAvatarUrl(),
                player.getStatus(),
                player.getGamesPlayed(),
                player.getWins(),
                player.getLosses(),
                player.getDraws(),
                player.getCreatedAt()
        );
    }
}
