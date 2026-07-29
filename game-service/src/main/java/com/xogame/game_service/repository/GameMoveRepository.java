package com.xogame.game_service.repository;

import com.xogame.game_service.domain.entities.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameMoveRepository extends JpaRepository<GameMove, UUID> {
}
