package com.fabrica.equipment.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic instrumentCreatedTopic() {
        return TopicBuilder.name("instrument-created-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
