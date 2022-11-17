package com.example.consumer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConfig {
    private final ConsumerFactory<Integer, String> consumerFactory;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String>
    kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, String>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(customError());
        return factory;
    }


    public DefaultErrorHandler customError() {
        var fixedBackOff = new FixedBackOff(1000L, 2L);
        var errorHandler = new DefaultErrorHandler(fixedBackOff);

        var toIgnoreExceptions = List.of(IllegalArgumentException.class);

        toIgnoreExceptions.forEach(errorHandler::addNotRetryableExceptions);
        errorHandler.setRetryListeners((consumerRecord, e, i)
                -> log.info("Failed record in Retry Listener, Exception : {} , deliveryAttempt : {} ",
                e.getMessage(), i));
        return errorHandler;
    }
}
