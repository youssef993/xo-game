package com.xogame.player_service.services;

import com.xogame.player_service.domain.Friendship;
import com.xogame.player_service.domain.FriendshipStatus;
import com.xogame.player_service.domain.Player;
import com.xogame.player_service.dto.FriendResponse;
import com.xogame.player_service.dto.FriendshipRequest;
import com.xogame.player_service.repository.FriendshipRepository;
import com.xogame.player_service.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final PlayerRepository playerRepository;

    public List<FriendResponse> sendFriendRequest(String recieverId, String senderId){
        Player reciever = playerRepository.findByKeycloakId(recieverId).get();
        Player sender = playerRepository.findByKeycloakId(senderId).get();
        Friendship friendship = new Friendship();
        friendship.setReceiver(reciever);
        friendship.setRequester(sender);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        return getFriends(sender.getKeycloakId());
    }

    public List<FriendResponse> getFriends(String keyclockId) {

        UUID playerId = playerRepository.findByKeycloakId(keyclockId).get().getId();
        return friendshipRepository
                .findAllByPlayerIdAndStatus(
                        playerId,
                        FriendshipStatus.ACCEPTED
                )
                .stream()
                .map(friendship -> {
                    Player friend;

                    if (friendship.getRequester().getId().equals(playerId)) {
                        friend = friendship.getReceiver();
                    } else {
                        friend = friendship.getRequester();
                    }

                    return new FriendResponse(
                            friend.getId(),
                            friend.getUsername(),
                            friend.getAvatarUrl(),
                            friend.getStatus(),
                            friend.getWins(),
                            friend.getLosses(),
                            friend.getDraws()
                    );
                })
                .toList();
    }
}
