package com.xogame.game_service.service;

import com.xogame.game_service.dto.kafka.ScoreEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamesProducer {

    private final KafkaTemplate<String, ScoreEvent> kafkaTemplate;

    @Value("${kafka.topics.games}")
    private String gamesTopic;

    public CompletableFuture<SendResult<String, ScoreEvent>> sendEvent(ScoreEvent scoreEvent){

        log.info("Sending event: {} to topic: {}", scoreEvent, gamesTopic);

        var feature = kafkaTemplate.send(gamesTopic, scoreEvent);

        feature.whenComplete((res, ex)->{
            if (ex != null){
                log.error("Error sending score: {}", scoreEvent, ex);
            }else {
                RecordMetadata metadata = res.getRecordMetadata();
                log.info("Game score sent successfully: {}", scoreEvent);
            }
        });
        return feature;
    }
}
