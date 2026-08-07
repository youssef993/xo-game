package com.xogame.player_service.services;

import com.xogame.player_service.domain.Player;
import com.xogame.player_service.dto.ScoreReponse;
import com.xogame.player_service.dto.kafka.ScoreEvent;
import com.xogame.player_service.repository.PlayerRepository;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaScoreConsumer {

    private final PlayerRepository playerRepository;
    private final PlayerScorePublisher playerScorePublisher;

    @KafkaListener(
            topics = "${kafka.topics.games}",
            groupId = "xogame-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumerGamesEvent(
            ConsumerRecord<String, ScoreEvent> record,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ){
        var event = record.value();

        log.info("recieved new score: {}", event);

        final String playerXId = event.playerXId();
        final String playerOId = event.playerOId();
        Player playerX = playerRepository.findByKeycloakId(playerXId).get();
        Player playerO = playerRepository.findByKeycloakId(playerOId).get();
        if (event.winnerId() == null || event.winnerId().isEmpty()){
            playerX.setDraws(playerX.getDraws() + 1);
            playerX.setGamesPlayed(playerX.getGamesPlayed() + 1);
            playerO.setDraws(playerO.getDraws() + 1);
            playerO.setGamesPlayed(playerO.getGamesPlayed() + 1);
        } else if(event.winnerId().equalsIgnoreCase(playerOId)) {
            playerO = updateWinnerPlayerStats(playerO);
            playerX = updateLoserPlayerStats(playerX);
        } else {
            playerX = updateWinnerPlayerStats(playerX);
            playerO = updateLoserPlayerStats(playerO);
        }

        playerRepository.save(playerX);
        playerRepository.save(playerO);
        playerScorePublisher.publish("Score_updated",
                new ScoreReponse(playerXId, playerOId));

    }

    private Player updateWinnerPlayerStats(Player player){
        player.setWins(player.getWins() + 1);
        player.setSerieActuelle( player.getSerieActuelle() + 1);
        player.setGamesPlayed(player.getGamesPlayed() + 1);
        if (player.getMeilleurSerie() < player.getSerieActuelle()) {
            player.setMeilleurSerie(player.getSerieActuelle());
        }
        return player;
    }
    private Player updateLoserPlayerStats(Player player){
        player.setLosses(player.getLosses() + 1);
        player.setGamesPlayed(player.getGamesPlayed() + 1);
        player.setSerieActuelle(0);
        return player;
    }

}
