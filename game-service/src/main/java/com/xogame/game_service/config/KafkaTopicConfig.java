package com.xogame.game_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.games}")
    private String gamesTopics;

    @Bean
    public NewTopic gamesTopic(){
        return TopicBuilder.name(gamesTopics)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
