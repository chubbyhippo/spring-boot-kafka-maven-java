package com.example.producer.controller;

import com.example.producer.domain.Book;
import com.example.producer.domain.LibraryEvent;
import com.example.producer.service.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = LibraryEventController.class)
class LibraryEventControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private LibraryEventProducer libraryEventProducer;

    @Test
    void shouldReturnLibraryEventWithPost() throws JsonProcessingException {

        when(libraryEventProducer.sendLibraryEvent(any())).thenReturn(null);
        Book book = Book.builder()
                .id(1)
                .author("author")
                .name("name")
                .build();
        LibraryEvent request = LibraryEvent.builder()
                .id(1)
                .book(book)
                .build();

        webTestClient.post()
                .uri("/v1/libraryevent")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

    }
}