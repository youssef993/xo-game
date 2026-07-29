package com.xogame.game_service.mapper;

import com.xogame.game_service.domain.entities.Game;
import com.xogame.game_service.domain.entities.GameMove;
import com.xogame.game_service.domain.enums.PlayerSymbol;
import com.xogame.game_service.dto.GameResponse;
import com.xogame.game_service.dto.MoveResponse;
import com.xogame.game_service.service.BoardCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameMapper {

    private final BoardCalculator boardCalculator;

    public GameResponse toResponse(Game game) {
        List<MoveResponse> moves = game.getMoves()
                .stream()
                .map(this::toMoveResponse)
                .toList();

        List<PlayerSymbol> board =
                boardCalculator.buildBoard(game.getMoves());

        return new GameResponse(
                game.getId(),
                game.getPlayerXId(),
                game.getPlayerOId(),
                board,
                game.getCurrentTurn(),
                game.getStatus(),
                game.getWinnerId(),
                game.getVersion(),
                game.getCreatedAt(),
                game.getStartedAt(),
                game.getFinishedAt(),
                moves
        );
    }

    private MoveResponse toMoveResponse(GameMove move) {
        return new MoveResponse(
                move.getId(),
                move.getPlayerId(),
                move.getSymbol(),
                move.getCellIndex(),
                move.getMoveNumber(),
                move.getPlayedAt()
        );
    }
}