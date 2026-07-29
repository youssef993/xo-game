package com.xogame.matchmaking_service.controller;

import com.xogame.matchmaking_service.dto.MatchmakingResponse;
import com.xogame.matchmaking_service.service.MatchmakingApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matchmaking")
@RequiredArgsConstructor
@Slf4j
public class MatchmakingController {

    private final MatchmakingApplicationService
            matchmakingService;

    @PostMapping("/search")
    public MatchmakingResponse search(
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("{} search for new match", jwt.getSubject());
        return matchmakingService.search(
                jwt.getSubject()
        );
    }

    @GetMapping("/status")
    public MatchmakingResponse getStatus(
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("{} search for new Status", jwt.getSubject());
        return matchmakingService.getStatus(
                jwt.getSubject()
        );
    }

    @DeleteMapping("/search")
    public MatchmakingResponse cancel(
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("{} cancel match", jwt.getSubject());
        return matchmakingService.cancel(
                jwt.getSubject()
        );
    }
}