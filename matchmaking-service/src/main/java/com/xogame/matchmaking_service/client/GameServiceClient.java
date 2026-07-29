package com.xogame.matchmaking_service.client;

import com.xogame.matchmaking_service.dto.CreateMatchedGameRequest;
import com.xogame.matchmaking_service.dto.CreatedGameResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameServiceClient {

    private final RestClient gameServiceRestClient;

    @Value("${services.game.base-url}")
    private String gameServiceBaseUrl;

    public CreatedGameResponse createMatchedGame(
            String playerXId,
            String playerOId
    ) {
        log.info("call game service to open a new game");
        CreatedGameResponse response =
                gameServiceRestClient
                        .post()
                        .uri(
                                gameServiceBaseUrl
                                        + "/internal/games/matched"
                        )
                        .body(
                                new CreateMatchedGameRequest(
                                        playerXId,
                                        playerOId
                                )
                        )
                        .retrieve()
                        .body(CreatedGameResponse.class);

        if (response == null
                || response.id() == null) {
            throw new IllegalStateException(
                    "Réponse invalide du game-service"
            );
        }

        return response;
    }
}
