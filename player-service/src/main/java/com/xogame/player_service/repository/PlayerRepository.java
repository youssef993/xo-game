package com.xogame.player_service.repository;

import com.xogame.player_service.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByUsername(String username);

    Optional<Player> findByKeycloakId(String keycloakId);

    @Query("""
    SELECT p
    FROM Player p
    WHERE p.keycloakId <> :playerId
      AND (
            LOWER(p.username) LIKE LOWER(CONCAT(:search, '%'))
         OR LOWER(p.email) LIKE LOWER(CONCAT(:search, '%'))
      )
    """)
    List<Player> searchPlayers(@Param("playerId") String playerId, @Param("search") String search);

    boolean existsByUsernameIgnoreCase(String username);
}
