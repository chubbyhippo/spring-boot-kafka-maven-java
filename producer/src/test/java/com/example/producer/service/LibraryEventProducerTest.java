package com.example.producer.service;

import com.example.producer.domain.Book;
import com.example.producer.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryEventProducerTest {

    @Mock
    private KafkaTemplate<Integer, String> kafkaTemplate;
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private LibraryEventProducer libraryEventProducer;

    @Test
    void shouldSendKafkaMessage() throws JsonProcessingException {
        var book = Book.builder()
                .id(1)
                .author("author")
                .name("name")
                .build();
        var libraryEvent = LibraryEvent.builder()
                .id(1)
                .book(book)
                .build();
        var listenableFuture = new CompletableFuture<SendResult<Integer, String>>();
        when(objectMapper.writeValueAsString(libraryEvent)).thenCallRealMethod();
        when(kafkaTemplate.sendDefault(anyInt(), anyString())).thenReturn(listenableFuture);
        libraryEventProducer.sendLibraryEvent(libraryEvent);
        verify(kafkaTemplate, times(1)).sendDefault(anyInt(), anyString());
    }

}