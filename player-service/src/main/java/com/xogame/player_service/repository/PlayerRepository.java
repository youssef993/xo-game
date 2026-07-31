package com.xogame.player_service.repository;

import com.xogame.player_service.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByUsername(String username);

    Optional<Player> findByKeycloakId(String keycloakId);

    boolean existsByUsernameIgnoreCase(String username);
}
