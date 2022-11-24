package com.example.consumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "topics")
@Getter
@Setter
public class TopicsProperties {
    private String retry;
    private String dlt;
}
