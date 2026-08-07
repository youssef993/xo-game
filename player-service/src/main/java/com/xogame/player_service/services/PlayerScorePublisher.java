package com.xogame.player_service.services;

import com.xogame.player_service.dto.ScoreReponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerScorePublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(String eventType, ScoreReponse score){
        log.info("sendScore: {}", score);
        messagingTemplate.convertAndSend("/topic/player/" + score.playerOId(), score);
        messagingTemplate.convertAndSend("/topic/player/" + score.playerXId(), score);
    }
}
