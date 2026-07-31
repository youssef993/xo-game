package com.xogame.game_service.websocket;

import com.xogame.game_service.dto.GameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class GameWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(
            String eventType,
            GameResponse game
    ) {
        GameUpdatedEvent event = new GameUpdatedEvent(
                eventType,
                game.id(),
                game,
                Instant.now()
        );

        messagingTemplate.convertAndSend(
                "/topic/games/" + game.id(),
                event
        );
    }
}