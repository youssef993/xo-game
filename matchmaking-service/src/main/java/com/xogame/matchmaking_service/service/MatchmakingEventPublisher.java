package com.xogame.matchmaking_service.service;

import com.xogame.matchmaking_service.domain.MatchmakingTicket;
import com.xogame.matchmaking_service.dto.MatchFoundEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MatchmakingEventPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic matchmakingTopic;

    public void publishMatchFound(
            MatchmakingTicket ticket
    ) {
        MatchFoundEvent event =
                new MatchFoundEvent(
                        "MATCH_FOUND",
                        ticket.getPlayerId(),
                        ticket.getOpponentId(),
                        ticket.getGameId(),
                        Instant.now()
                );

        try {
            redisTemplate.convertAndSend(
                    matchmakingTopic.getTopic(),
                    objectMapper.writeValueAsString(
                            event
                    )
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Impossible de publier MATCH_FOUND",
                    exception
            );
        }
    }
}