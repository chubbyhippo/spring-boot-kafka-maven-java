package com.example.consumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
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
        return new DefaultErrorHandler(fixedBackOff);
    }
}
