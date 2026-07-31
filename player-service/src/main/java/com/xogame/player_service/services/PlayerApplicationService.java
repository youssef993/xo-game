package com.xogame.player_service.services;

import com.xogame.player_service.domain.Player;
import com.xogame.player_service.domain.PlayerStatus;
import com.xogame.player_service.dto.PlayerResponse;
import com.xogame.player_service.exception.PlayerNotFoundException;
import com.xogame.player_service.mapper.PlayerMapper;
import com.xogame.player_service.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerApplicationService {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Transactional
    public PlayerResponse registerOrGet(
            Jwt jwt
    ) {
        String keycloakId = jwt.getSubject();

        return playerRepository.findByKeycloakId(keycloakId)
                .map(playerMapper::toResponse)
                .orElseGet(() -> createPlayer(jwt));
    }

    @Transactional
    public PlayerResponse getCurrentPlayer(
            String keycloakId
    ) {
        return playerRepository
                .findByKeycloakId(keycloakId)
                .map(playerMapper::toResponse)
                .orElseThrow(() ->
                        new PlayerNotFoundException(
                                "Profil joueur introuvable"
                        )
                );
    }

    private PlayerResponse createPlayer(Jwt jwt) {
        Instant now = Instant.now();
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String username = generateUniqueUsername(preferredUsername,jwt.getSubject());

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .keycloakId(jwt.getSubject())
                .username(username)
                .email(email != null? email : "")
                .status(PlayerStatus.ONLINE)
                .gamesPlayed(0)
                .wins(0)
                .losses(0)
                .draws(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return playerMapper.toResponse(playerRepository.save(player));
    }

    private String generateUniqueUsername(String preferredUsername,String keycloakId) {
        String base = preferredUsername != null && !preferredUsername.isBlank()
                ? preferredUsername : "player";

        if (!playerRepository.existsByUsernameIgnoreCase(base)) {
            return base;
        }

        return base + "-" + keycloakId.substring(0, 6);
    }
}
