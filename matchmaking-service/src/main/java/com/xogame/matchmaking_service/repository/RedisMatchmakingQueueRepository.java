package com.xogame.matchmaking_service.repository;

import com.xogame.matchmaking_service.domain.MatchmakingTicket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisMatchmakingQueueRepository
        implements MatchmakingQueueRepository {

    private static final String QUEUE_KEY =
            "matchmaking:queue";

    private static final String TICKET_PREFIX =
            "matchmaking:ticket:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${matchmaking.ticket-ttl:10m}")
    private Duration ticketTtl;

    private final DefaultRedisScript<String>
            popOpponentScript =
            createPopOpponentScript();

    @Override
    public Optional<MatchmakingTicket> findByPlayerId(
            String playerId
    ) {
        log.info("search for new match in redis");
        String json = redisTemplate.opsForValue()
                .get(ticketKey(playerId));

        if (json == null) {
            return Optional.empty();
        }

        return Optional.of(
                deserialize(json)
        );
    }

    @Override
    public void enqueue(
            MatchmakingTicket ticket
    ) {
        String key =
                ticketKey(ticket.getPlayerId());

        /*
         * setIfAbsent empêche un même joueur
         * de créer plusieurs tickets.
         */
        Boolean created =
                redisTemplate.opsForValue()
                        .setIfAbsent(
                                key,
                                serialize(ticket),
                                ticketTtl
                        );

        if (Boolean.TRUE.equals(created)) {
            redisTemplate.opsForList()
                    .rightPush(
                            QUEUE_KEY,
                            ticket.getPlayerId()
                    );
        }
    }

    @Override
    public Optional<String> popOpponent(
            String currentPlayerId
    ) {
        String opponentId =
                redisTemplate.execute(
                        popOpponentScript,
                        List.of(QUEUE_KEY),
                        TICKET_PREFIX,
                        currentPlayerId
                );

        return Optional.ofNullable(
                opponentId
        );
    }

    @Override
    public void save(
            MatchmakingTicket ticket
    ) {
        redisTemplate.opsForValue()
                .set(
                        ticketKey(ticket.getPlayerId()),
                        serialize(ticket),
                        ticketTtl
                );
    }

    @Override
    public void remove(
            String playerId
    ) {
        redisTemplate.delete(
                ticketKey(playerId)
        );

        redisTemplate.opsForList()
                .remove(
                        QUEUE_KEY,
                        0,
                        playerId
                );
    }

    private static DefaultRedisScript<String>
    createPopOpponentScript() {

        DefaultRedisScript<String> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        "redis/pop-opponent.lua"
                )
        );

        script.setResultType(String.class);

        return script;
    }

    private String ticketKey(
            String playerId
    ) {
        return TICKET_PREFIX + playerId;
    }

    private String serialize(
            MatchmakingTicket ticket
    ) {
        try {
            return objectMapper
                    .writeValueAsString(ticket);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Impossible de sérialiser le ticket",
                    exception
            );
        }
    }

    private MatchmakingTicket deserialize(
            String json
    ) {
        try {
            return objectMapper.readValue(
                    json,
                    MatchmakingTicket.class
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Ticket Redis invalide",
                    exception
            );
        }
    }
}