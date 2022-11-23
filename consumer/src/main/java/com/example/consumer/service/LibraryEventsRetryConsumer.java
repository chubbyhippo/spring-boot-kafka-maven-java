package com.example.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventsRetryConsumer {
    private final LibraryEventService libraryEventService;

    @KafkaListener(topics = {"${topics.retry}"},
            groupId = "retry-listener-group",
            autoStartup = "${retryListener.startup:true}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("ConsumerRecord in Retry consumer: {} ", consumerRecord);
        consumerRecord.headers()
                .forEach(header -> log.info("Key : {}, Value : {}", header.key(), new String(header.value())));
        libraryEventService.processLibraryEvent(consumerRecord);
    }
}
