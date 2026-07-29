package com.xogame.matchmaking_service.repository;

import com.xogame.matchmaking_service.domain.MatchmakingTicket;

import java.util.Optional;

public interface MatchmakingQueueRepository {

    Optional<MatchmakingTicket> findByPlayerId(
            String playerId
    );

    void enqueue(
            MatchmakingTicket ticket
    );

    Optional<String> popOpponent(
            String currentPlayerId
    );

    void save(
            MatchmakingTicket ticket
    );

    void remove(
            String playerId
    );
}