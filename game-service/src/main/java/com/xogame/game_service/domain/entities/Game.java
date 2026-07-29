package com.xogame.game_service.domain.entities;

import com.xogame.game_service.domain.enums.GameStatus;
import com.xogame.game_service.domain.enums.PlayerSymbol;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    private UUID id;

    @Column(name = "player_x_id", nullable = false, length = 100)
    private String playerXId;

    @Column(name = "player_o_id", length = 100)
    private String playerOId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_turn", length = 1)
    private PlayerSymbol currentTurn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GameStatus status;

    @Column(name = "winner_id", length = 100)
    private String winnerId;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("moveNumber ASC")
    @Builder.Default
    private List<GameMove> moves = new ArrayList<>();

    public void addMove(GameMove move) {
        moves.add(move);
        move.setGame(this);
    }
}