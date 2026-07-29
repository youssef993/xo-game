package com.xogame.game_service.controller;

import com.xogame.game_service.dto.GameResponse;
import com.xogame.game_service.dto.PlayMoveRequest;
import com.xogame.game_service.service.GameApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameApplicationService gameService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    public GameResponse createGame(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String playerId = jwt.getSubject();

        return gameService.createGame(playerId);
    }


    @PostMapping("/{gameId}/join")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    public GameResponse joinGame(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String playerId = jwt.getSubject();

        return gameService.joinGame(gameId, playerId);
    }


    @GetMapping("/waiting")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    public List<GameResponse> findWaitingGames(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return gameService.findWaitingGames(jwt.getSubject());
    }


    @GetMapping("/{gameId}")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    public GameResponse getGame(
            @PathVariable UUID gameId
    ) {
        return gameService.getGame(gameId);
    }


    @PostMapping("/{gameId}/moves")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    public GameResponse playMove(
            @PathVariable UUID gameId,
            @Valid @RequestBody PlayMoveRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String playerId = jwt.getSubject();

        return gameService.playMove(
                gameId,
                playerId,
                request
        );
    }


    @PostMapping("/{gameId}/abandon")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN')")
    public GameResponse abandon(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String playerId = jwt.getSubject();

        return gameService.abandon(gameId, playerId);
    }
}