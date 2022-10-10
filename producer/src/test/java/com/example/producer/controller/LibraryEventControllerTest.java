package com.example.producer.controller;

import com.example.producer.domain.Book;
import com.example.producer.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(controllers = LibraryEventController.class)
class LibraryEventControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnLibraryEvent() {
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