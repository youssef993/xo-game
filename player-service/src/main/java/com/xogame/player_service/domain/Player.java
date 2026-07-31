package com.xogame.player_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "players",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_player_keycloak_id",
                        columnNames = "keycloak_id"
                ),
                @UniqueConstraint(
                        name = "uk_player_username",
                        columnNames = "username"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    private UUID id;

    @Column(name = "keycloak_id",nullable = false,updatable = false)
    private String keycloakId;

    @Column(nullable = false,length = 50)
    private String username;

    @Column(nullable = false,length = 150)
    private String email;

    @Column(name = "avatar_url",length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerStatus status;

    @Column(name = "games_played",nullable = false)
    private int gamesPlayed;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    @Column(nullable = false)
    private int draws;

    @Column(name = "created_at",nullable = false,updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at",nullable = false)
    private Instant updatedAt;
}

