package com.xogame.matchmaking_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchmakingTicket {

    private UUID ticketId;

    private String playerId;

    private MatchmakingStatus status;

    private Instant createdAt;

    private String opponentId;

    private UUID gameId;

    private Instant matchedAt;
}