package com.xogame.player_service.controllers;

import com.xogame.player_service.dto.FriendResponse;
import com.xogame.player_service.dto.FriendshipRequest;
import com.xogame.player_service.services.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @GetMapping
    public List<FriendResponse> getFriends(@AuthenticationPrincipal Jwt jwt) {
        return friendshipService.getFriends(jwt.getSubject());
    }

    @PostMapping
    public List<FriendResponse> sendFriendRequest(@RequestBody FriendshipRequest friendshipRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return friendshipService.sendFriendRequest(friendshipRequest.recieverId(), jwt.getSubject());
    }
}
