package com.xogame.player_service.mapper;

import com.xogame.player_service.domain.Player;
import com.xogame.player_service.dto.PlayerResponse;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public PlayerResponse toResponse(Player player) {

        var score = player.getWins() * 25 + player.getDraws() * 5 - player.getLosses() * 15;
        var taux = player.getGamesPlayed() != 0 ? player.getWins() / player.getGamesPlayed() : 0 ;
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
                score,
                player.getMeilleurSerie(),
                player.getSerieActuelle(),
                taux,
                player.getCreatedAt()
        );
    }
}
