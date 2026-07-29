package com.xogame.matchmaking_service.service;

import com.xogame.matchmaking_service.client.GameServiceClient;
import com.xogame.matchmaking_service.domain.MatchmakingStatus;
import com.xogame.matchmaking_service.domain.MatchmakingTicket;
import com.xogame.matchmaking_service.dto.CreatedGameResponse;
import com.xogame.matchmaking_service.dto.MatchmakingResponse;
import com.xogame.matchmaking_service.exception.MatchmakingException;
import com.xogame.matchmaking_service.repository.MatchmakingQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchmakingApplicationService {

    private final MatchmakingQueueRepository queueRepository;
    private final GameServiceClient gameServiceClient;
    private final MatchmakingEventPublisher eventPublisher;

    public MatchmakingResponse search(
            String playerId
    ) {
        log.info("start looking for existing game");
        Optional<MatchmakingTicket> existing =
                queueRepository.findByPlayerId(playerId);

        log.info(existing.isPresent()? existing.get().toString() : "");

        if (existing.isPresent()) {
            if (existing.get().getStatus() == MatchmakingStatus.SEARCHING) {
                return toResponse(existing.get());
            }else {
                queueRepository.remove(playerId);
            }
        }
        log.info("open new game");
        Optional<String> opponent =
                queueRepository.popOpponent(playerId);

        if (opponent.isEmpty()) {
            log.info("open new game");
            return enqueue(playerId);
        }
        log.info("create new game");
        return createMatch(
                playerId,
                opponent.get()
        );
    }

    public MatchmakingResponse getStatus(
            String playerId
    ) {
        MatchmakingTicket ticket =
                queueRepository
                        .findByPlayerId(playerId)
                        .orElseThrow(() ->
                                new MatchmakingException(
                                        "Aucune recherche active"
                                )
                        );

        return toResponse(ticket);
    }

    public MatchmakingResponse cancel(
            String playerId
    ) {
        MatchmakingTicket ticket =
                queueRepository
                        .findByPlayerId(playerId)
                        .orElseThrow(() ->
                                new MatchmakingException(
                                        "Aucune recherche active"
                                )
                        );

        if (ticket.getStatus()
                == MatchmakingStatus.MATCHED) {
            throw new MatchmakingException(
                    "Le matchmaking est déjà terminé"
            );
        }

        ticket.setStatus(
                MatchmakingStatus.CANCELLED
        );

        queueRepository.remove(
                playerId
        );

        return toResponse(ticket);
    }

    private MatchmakingResponse toResponse(
            MatchmakingTicket ticket
    ) {
        return new MatchmakingResponse(
                ticket.getTicketId(),
                ticket.getStatus(),
                ticket.getPlayerId(),
                ticket.getOpponentId(),
                ticket.getGameId(),
                ticket.getCreatedAt(),
                ticket.getMatchedAt()
        );
    }

    private MatchmakingResponse enqueue(
            String playerId
    ) {
        MatchmakingTicket ticket =
                MatchmakingTicket.builder()
                        .ticketId(UUID.randomUUID())
                        .playerId(playerId)
                        .status(
                                MatchmakingStatus.SEARCHING
                        )
                        .createdAt(Instant.now())
                        .build();

        queueRepository.enqueue(ticket);

        /*
         * Un autre replica peut avoir créé le ticket
         * entre la vérification et l'enqueue.
         */
        return queueRepository
                .findByPlayerId(playerId)
                .map(this::toResponse)
                .orElseThrow();
    }

    private MatchmakingResponse createMatch(
            String playerId,
            String opponentId
    ) {
        try {
            log.info("create new game");
            CreatedGameResponse createdGame =
                    gameServiceClient.createMatchedGame(
                            opponentId,
                            playerId
                    );

            Instant now = Instant.now();

            MatchmakingTicket playerTicket =
                    matchedTicket(
                            playerId,
                            opponentId,
                            createdGame.id(),
                            now
                    );

            MatchmakingTicket opponentTicket =
                    matchedTicket(
                            opponentId,
                            playerId,
                            createdGame.id(),
                            now
                    );

            queueRepository.save(playerTicket);
            queueRepository.save(opponentTicket);

            eventPublisher.publishMatchFound(
                    playerTicket
            );

            eventPublisher.publishMatchFound(
                    opponentTicket
            );

            return toResponse(playerTicket);

        } catch (RuntimeException exception) {

            /*
             * Le joueur extrait doit être réinséré
             * si la création du game échoue.
             */
            MatchmakingTicket restoredTicket =
                    MatchmakingTicket.builder()
                            .ticketId(UUID.randomUUID())
                            .playerId(opponentId)
                            .status(
                                    MatchmakingStatus.SEARCHING
                            )
                            .createdAt(Instant.now())
                            .build();

            queueRepository.enqueue(
                    restoredTicket
            );

            throw exception;
        }
    }

    private MatchmakingTicket matchedTicket(
            String playerId,
            String opponentId,
            UUID gameId,
            Instant matchedAt
    ) {
        return MatchmakingTicket.builder()
                .ticketId(UUID.randomUUID())
                .playerId(playerId)
                .opponentId(opponentId)
                .gameId(gameId)
                .status(MatchmakingStatus.MATCHED)
                .createdAt(matchedAt)
                .matchedAt(matchedAt)
                .build();
    }
}