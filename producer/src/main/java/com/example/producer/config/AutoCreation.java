package com.example.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreation {
    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name("library-events")
                .partitions(2)
                .replicas(2)
                .build();
    }
}
