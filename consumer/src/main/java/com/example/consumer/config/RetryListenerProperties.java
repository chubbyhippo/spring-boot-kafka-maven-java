package com.example.consumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "retry-listener")
@Component
@Getter
@Setter
public class RetryListenerProperties {
    private boolean startup = true;
}
