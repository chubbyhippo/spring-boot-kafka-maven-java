package com.example.producer;

import com.example.producer.domain.Book;
import com.example.producer.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-event"})
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class ProducerApplicationTests {


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
