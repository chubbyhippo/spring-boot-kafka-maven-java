package com.example.consumer.service;

import com.example.consumer.entity.LibraryEvent;
import com.example.consumer.repository.LibraryEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventService {

    private final ObjectMapper objectMapper;
    private final LibraryEventRepository libraryEventRepository;

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        var libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("libraryEvent : {} ", libraryEvent);
        switch (libraryEvent.getLibraryEventType()) {
            case NEW -> save(libraryEvent);

            case UPDATE -> {
            }
            default -> log.info("Invalid Library Event Type");
        }
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventRepository.save(libraryEvent);
        log.info("Successfully saved the library Event {} ", libraryEvent);
    }
}
