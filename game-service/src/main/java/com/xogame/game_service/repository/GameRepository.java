package com.xogame.game_service.repository;

import com.xogame.game_service.domain.entities.Game;
import com.xogame.game_service.domain.enums.GameStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    @EntityGraph(attributePaths = "moves")
    @Query("""
        select g
        from Game g
        where g.id = :gameId
    """)
    Optional<Game> findWithMovesById(@Param("gameId") UUID gameId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "moves")
    @Query("""
        select g
        from Game g
        where g.id = :gameId
    """)
    Optional<Game> findByIdForUpdate(@Param("gameId") UUID gameId);

    @EntityGraph(attributePaths = "moves")
    List<Game> findTop20ByStatusAndPlayerXIdNotOrderByCreatedAtAsc(
            GameStatus status,
            String playerId
    );
}
