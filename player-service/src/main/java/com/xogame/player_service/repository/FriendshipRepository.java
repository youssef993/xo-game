package com.xogame.player_service.repository;

import com.xogame.player_service.domain.Friendship;
import com.xogame.player_service.domain.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    @Query("""
        SELECT f
        FROM Friendship f
        WHERE f.status = :status
          AND (
              f.requester.id = :playerId
              OR f.receiver.id = :playerId
          )
    """)
    List<Friendship> findAllByPlayerIdAndStatus(
            @Param("playerId") UUID playerId,
            @Param("status") FriendshipStatus status
    );
}
