package com.xogame.matchmaking_service.service;


import com.xogame.matchmaking_service.dto.MatchFoundEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchmakingRedisSubscriber {

    private final ObjectMapper objectMapper;

    private final SimpMessagingTemplate
            messagingTemplate;

    public void onMessage(
            String json
    ) {
        try {
            MatchFoundEvent event =
                    objectMapper.readValue(
                            json,
                            MatchFoundEvent.class
                    );

            /*
             * playerId doit correspondre au Principal.name
             * de la session STOMP, donc au claim sub.
             */
            messagingTemplate.convertAndSendToUser(
                    event.playerId(),
                    "/queue/matchmaking",
                    event
            );

        } catch (Exception exception) {
            log.error(
                    "Impossible de traiter l'événement Redis : {}",
                    json,
                    exception
            );
        }
    }
}