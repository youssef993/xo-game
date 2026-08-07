package com.xogame.player_service.controllers;

import com.xogame.player_service.dto.PlayerResponse;
import com.xogame.player_service.services.PlayerApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerApplicationService playerApplicationService;

    @PostMapping("/me/register")
    public PlayerResponse register(@AuthenticationPrincipal Jwt jwt){
        return playerApplicationService.registerOrGet(jwt);
    }

    @GetMapping("/me")
    public PlayerResponse getCurrentPlayer(@AuthenticationPrincipal Jwt jwt){
        return playerApplicationService.getCurrentPlayer(jwt.getSubject());
    }

    @GetMapping
    public List<PlayerResponse> getListPlayers(@RequestParam String search, @AuthenticationPrincipal Jwt jwt){
        return playerApplicationService.getListPlayerByUsernameOrEmail(jwt.getSubject(), search);
    }
}
