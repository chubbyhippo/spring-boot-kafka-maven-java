package com.example.producer.service;

import com.example.producer.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventProducer {
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.getId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        var sendResultCompletableFuture = kafkaTemplate.sendDefault(key, value);
        sendResultCompletableFuture.thenAccept(integerStringSendResult -> handleSuccess(key,
                        value,
                        integerStringSendResult))
                .exceptionally(throwable -> {
                    handleFailure(key, value, throwable);
                    try {
                        throw throwable;
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });

        return sendResultCompletableFuture;
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message for key : {}, and value is {},and the exception is {}", key, value,
                ex.getMessage());
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message has been sent successfully for the key : {}, and the value is {}, and partition is {}", key,
                value, result.getRecordMetadata().partition());
    }


}
