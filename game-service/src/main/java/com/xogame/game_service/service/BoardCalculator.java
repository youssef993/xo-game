package com.xogame.game_service.service;

import com.xogame.game_service.domain.entities.GameMove;
import com.xogame.game_service.domain.enums.PlayerSymbol;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BoardCalculator {

    private static final int BOARD_SIZE = 9;

    private static final int[][] WINNING_COMBINATIONS = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    public List<PlayerSymbol> buildBoard(List<GameMove> moves) {
        List<PlayerSymbol> board =
                new ArrayList<>(Collections.nCopies(BOARD_SIZE, null));

        for (GameMove move : moves) {
            board.set(move.getCellIndex(), move.getSymbol());
        }

        return board;
    }

    public boolean isCellOccupied(
            List<PlayerSymbol> board,
            int cellIndex
    ) {
        return board.get(cellIndex) != null;
    }

    public Optional<PlayerSymbol> findWinner(
            List<PlayerSymbol> board
    ) {
        for (int[] combination : WINNING_COMBINATIONS) {
            PlayerSymbol first = board.get(combination[0]);

            if (first != null
                    && first == board.get(combination[1])
                    && first == board.get(combination[2])) {
                return Optional.of(first);
            }
        }

        return Optional.empty();
    }

    public boolean isDraw(List<PlayerSymbol> board) {
        return board.stream().allMatch(symbol -> symbol != null)
                && findWinner(board).isEmpty();
    }
}
