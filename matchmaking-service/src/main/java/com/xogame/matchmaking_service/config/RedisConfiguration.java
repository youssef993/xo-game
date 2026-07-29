package com.xogame.matchmaking_service.config;

import com.xogame.matchmaking_service.service.MatchmakingRedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    @Bean
    StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        return new StringRedisTemplate(
                connectionFactory
        );
    }

    @Bean
    ChannelTopic matchmakingTopic() {
        return new ChannelTopic(
                "matchmaking:events"
        );
    }

    @Bean
    MessageListenerAdapter matchmakingListenerAdapter(
            MatchmakingRedisSubscriber subscriber
    ) {
        return new MessageListenerAdapter(
                subscriber,
                "onMessage"
        );
    }

    @Bean
    RedisMessageListenerContainer
    redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter,
            ChannelTopic matchmakingTopic
    ) {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(
                connectionFactory
        );

        container.addMessageListener(
                listenerAdapter,
                matchmakingTopic
        );

        return container;
    }
}