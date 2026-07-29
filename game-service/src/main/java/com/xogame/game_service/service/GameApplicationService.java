package com.xogame.game_service.service;

import com.xogame.game_service.domain.entities.Game;
import com.xogame.game_service.domain.entities.GameMove;
import com.xogame.game_service.domain.enums.GameStatus;
import com.xogame.game_service.domain.enums.PlayerSymbol;
import com.xogame.game_service.dto.GameResponse;
import com.xogame.game_service.dto.PlayMoveRequest;
import com.xogame.game_service.exception.GameNotFoundException;
import com.xogame.game_service.exception.InvalidGameActionException;
import com.xogame.game_service.mapper.GameMapper;
import com.xogame.game_service.repository.GameRepository;
import com.xogame.game_service.websocket.GameEventType;
import com.xogame.game_service.websocket.GameStateChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameApplicationService {

    private final GameRepository gameRepository;
    private final BoardCalculator boardCalculator;
    private final GameMapper gameMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public GameResponse createGame(String playerId) {
        Game game = Game.builder()
                .id(UUID.randomUUID())
                .playerXId(playerId)
                .currentTurn(PlayerSymbol.X)
                .status(GameStatus.WAITING_FOR_PLAYER)
                .createdAt(Instant.now())
                .build();

        return saveAndPublish(
                game,
                GameEventType.GAME_CREATED
        );
    }

    @Transactional
    public GameResponse joinGame(
            UUID gameId,
            String playerId
    ) {
        Game game = getGameForUpdate(gameId);

        if (game.getStatus() != GameStatus.WAITING_FOR_PLAYER) {
            throw new InvalidGameActionException(
                    "Cette partie n'attend plus de joueur"
            );
        }

        if (playerId.equals(game.getPlayerXId())) {
            throw new InvalidGameActionException(
                    "Le créateur ne peut pas rejoindre sa propre partie"
            );
        }

        game.setPlayerOId(playerId);
        game.setCurrentTurn(PlayerSymbol.X);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setStartedAt(Instant.now());

        return saveAndPublish(
                game,
                GameEventType.PLAYER_JOINED
        );
    }

    @Transactional(readOnly = true)
    public List<GameResponse> findWaitingGames(String playerId) {
        return gameRepository
                .findTop20ByStatusAndPlayerXIdNotOrderByCreatedAtAsc(
                        GameStatus.WAITING_FOR_PLAYER,
                        playerId
                )
                .stream()
                .map(gameMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameResponse getGame(UUID gameId) {
        Game game = gameRepository.findWithMovesById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        return gameMapper.toResponse(game);
    }

    @Transactional
    public GameResponse playMove(
            UUID gameId,
            String playerId,
            PlayMoveRequest request
    ) {
        Game game = getGameForUpdate(gameId);

        validateGameCanReceiveMove(game);

        PlayerSymbol playerSymbol =
                resolvePlayerSymbol(game, playerId);

        validatePlayerTurn(game, playerSymbol);

        List<PlayerSymbol> board =
                boardCalculator.buildBoard(game.getMoves());

        if (boardCalculator.isCellOccupied(
                board,
                request.cellIndex()
        )) {
            throw new InvalidGameActionException(
                    "Cette case est déjà occupée"
            );
        }

        GameMove move = GameMove.builder()
                .id(UUID.randomUUID())
                .playerId(playerId)
                .symbol(playerSymbol)
                .cellIndex(request.cellIndex())
                .moveNumber(game.getMoves().size() + 1)
                .playedAt(Instant.now())
                .build();

        game.addMove(move);
        board.set(request.cellIndex(), playerSymbol);

        updateGameStateAfterMove(
                game,
                board,
                playerSymbol,
                playerId
        );

        String eventType =
                game.getStatus().isFinished()
                        ? GameEventType.GAME_FINISHED
                        : GameEventType.MOVE_PLAYED;

        return saveAndPublish(game, eventType);
    }

    @Transactional
    public GameResponse abandon(
            UUID gameId,
            String playerId
    ) {
        Game game = getGameForUpdate(gameId);

        if (game.getStatus().isFinished()) {
            throw new InvalidGameActionException(
                    "Cette partie est déjà terminée"
            );
        }

        PlayerSymbol playerSymbol =
                resolvePlayerSymbol(game, playerId);

        game.setStatus(GameStatus.ABANDONED);

        String opponentId =
                playerSymbol == PlayerSymbol.X
                        ? game.getPlayerOId()
                        : game.getPlayerXId();

        game.setWinnerId(opponentId);
        game.setCurrentTurn(null);
        game.setFinishedAt(Instant.now());

        return saveAndPublish(
                game,
                GameEventType.GAME_ABANDONED
        );
    }

    @Transactional
    public GameResponse createMatchedGame(
            String playerXId,
            String playerOId
    ) {
        if (playerXId.equals(playerOId)) {
            throw new InvalidGameActionException(
                    "Les deux joueurs doivent être différents"
            );
        }

        Instant now = Instant.now();

        Game game = Game.builder()
                .id(UUID.randomUUID())
                .playerXId(playerXId)
                .playerOId(playerOId)
                .currentTurn(PlayerSymbol.X)
                .status(GameStatus.IN_PROGRESS)
                .createdAt(now)
                .startedAt(now)
                .build();

        return saveAndPublish(
                game,
                GameEventType.GAME_CREATED
        );
    }

    private Game getGameForUpdate(UUID gameId) {
        return gameRepository.findByIdForUpdate(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    private void validateGameCanReceiveMove(Game game) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidGameActionException(
                    "La partie n'est pas en cours"
            );
        }
    }

    private PlayerSymbol resolvePlayerSymbol(
            Game game,
            String playerId
    ) {
        if (playerId.equals(game.getPlayerXId())) {
            return PlayerSymbol.X;
        }

        if (playerId.equals(game.getPlayerOId())) {
            return PlayerSymbol.O;
        }

        throw new InvalidGameActionException(
                "Le joueur ne participe pas à cette partie"
        );
    }

    private void validatePlayerTurn(
            Game game,
            PlayerSymbol playerSymbol
    ) {
        if (game.getCurrentTurn() != playerSymbol) {
            throw new InvalidGameActionException(
                    "Ce n'est pas le tour de ce joueur"
            );
        }
    }

    private void updateGameStateAfterMove(
            Game game,
            List<PlayerSymbol> board,
            PlayerSymbol playerSymbol,
            String playerId
    ) {
        boolean winner = boardCalculator
                .findWinner(board)
                .filter(symbol -> symbol == playerSymbol)
                .isPresent();

        if (winner) {
            game.setStatus(
                    playerSymbol == PlayerSymbol.X
                            ? GameStatus.X_WON
                            : GameStatus.O_WON
            );

            game.setWinnerId(playerId);
            game.setCurrentTurn(null);
            game.setFinishedAt(Instant.now());
            return;
        }

        if (boardCalculator.isDraw(board)) {
            game.setStatus(GameStatus.DRAW);
            game.setWinnerId(null);
            game.setCurrentTurn(null);
            game.setFinishedAt(Instant.now());
            return;
        }

        game.setCurrentTurn(playerSymbol.opposite());
    }

    private GameResponse saveAndPublish(
            Game game,
            String eventType
    ) {
        Game savedGame = gameRepository.save(game);
        GameResponse response =
                gameMapper.toResponse(savedGame);

        eventPublisher.publishEvent(
                new GameStateChangedEvent(
                        eventType,
                        response
                )
        );

        return response;
    }
}