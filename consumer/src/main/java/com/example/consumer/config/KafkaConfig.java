package com.example.consumer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConfig {
    private final ConsumerFactory<Integer, String> consumerFactory;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @Value("${topics.retry}")
    private String retryTopic;
    @Value("${topics.dlt}")
    private String deadLetterTopic;

    public DeadLetterPublishingRecoverer publishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> {
                    if (e.getCause() instanceof RecoverableDataAccessException) {
                        return new TopicPartition(retryTopic, r.partition());
                    } else {
                        return new TopicPartition(deadLetterTopic, r.partition());
                    }
                });
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String>
    kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, String>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(customError());
        return factory;
    }


    public DefaultErrorHandler customError() {
//        var fixedBackOff = new FixedBackOff(1000L, 2L);
        var exponentialBackOffWithMaxRetries = new ExponentialBackOffWithMaxRetries(2);
        exponentialBackOffWithMaxRetries.setInitialInterval(1_000L);
        exponentialBackOffWithMaxRetries.setMultiplier(2.0);
        exponentialBackOffWithMaxRetries.setMaxInterval(2_000);

        var errorHandler = new DefaultErrorHandler(
                publishingRecoverer(),
//                fixedBackOff
                exponentialBackOffWithMaxRetries
        );

        var toIgnoreExceptions = List.of(IllegalArgumentException.class);
        toIgnoreExceptions.forEach(errorHandler::addNotRetryableExceptions);

        var toRetryExceptions = List.of(RecoverableDataAccessException.class);
        toRetryExceptions.forEach(errorHandler::addRetryableExceptions);

        errorHandler.setRetryListeners((consumerRecord, e, i)
                -> log.info("Failed record in Retry Listener, Exception : {} , deliveryAttempt : {} ",
                e.getMessage(), i));
        return errorHandler;
    }
}
