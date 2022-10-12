package com.example.producer;

import com.example.producer.domain.Book;
import com.example.producer.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"})
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class ProducerApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        var configs = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void shouldReturnLibraryEvent() {
        var book = Book.builder()
                .id(1)
                .author("author")
                .name("name")
                .build();
        var request = LibraryEvent.builder()
                .id(1)
                .book(book)
                .build();

        webTestClient.post()
                .uri("/v1/libraryevent")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        assertThat(singleRecord.value()).isNotNull();

    }

    @Test
    void shouldReturn400() {

        var book = Book.builder()
                .id(null)
                .author(null)
                .name("name")
                .build();
        var request = LibraryEvent.builder()
                .id(null)
                .book(book)
                .build();

        webTestClient.post()
                .uri("/v1/libraryevent")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("book.author - must not be blank, book.id - must not be null");

    }

}
