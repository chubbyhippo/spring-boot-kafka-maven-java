package com.example.consumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "retry-listener")
@Getter
@Setter
public class RetryListenerProperties {
    private boolean startup;
}
