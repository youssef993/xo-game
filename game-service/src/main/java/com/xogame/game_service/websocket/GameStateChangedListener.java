package com.xogame.game_service.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class GameStateChangedListener {

    private final GameWebSocketPublisher webSocketPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGameStateChanged(GameStateChangedEvent event) {
        webSocketPublisher.publish(event.eventType(),event.game());
    }
}
