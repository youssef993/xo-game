package com.xogame.game_service.controller;

import com.xogame.game_service.dto.CreateMatchedGameRequest;
import com.xogame.game_service.dto.GameResponse;
import com.xogame.game_service.service.GameApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/games")
@RequiredArgsConstructor
public class InternalGameController {

    private final GameApplicationService gameService;

    @PostMapping("/matched")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createMatchedGame(
            @Valid
            @RequestBody
            CreateMatchedGameRequest request
    ) {
        return gameService.createMatchedGame(
                request.playerXId(),
                request.playerOId()
        );
    }
}