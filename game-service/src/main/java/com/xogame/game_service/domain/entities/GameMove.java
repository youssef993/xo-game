package com.xogame.game_service.domain.entities;

import com.xogame.game_service.domain.enums.PlayerSymbol;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "game_moves",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_game_cell",
                        columnNames = {"game_id", "cell_index"}
                ),
                @UniqueConstraint(
                        name = "uk_game_move_number",
                        columnNames = {"game_id", "move_number"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameMove {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "player_id", nullable = false, length = 100)
    private String playerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private PlayerSymbol symbol;

    @Column(name = "cell_index", nullable = false)
    private Integer cellIndex;

    @Column(name = "move_number", nullable = false)
    private Integer moveNumber;

    @Column(name = "played_at", nullable = false)
    private Instant playedAt;
}