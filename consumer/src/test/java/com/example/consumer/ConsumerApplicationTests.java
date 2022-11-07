package com.example.consumer;

import com.example.consumer.entity.LibraryEventType;
import com.example.consumer.repository.LibraryEventRepository;
import com.example.consumer.service.LibraryEventService;
import com.example.consumer.service.LibraryEventsConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"})
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ConsumerApplicationTests {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;
    @SpyBean
    private LibraryEventsConsumer libraryEventsConsumer;
    @SpyBean
    private LibraryEventService libraryEventService;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private LibraryEventRepository libraryEventRepository;

    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    void shouldPublishNewLibraryEvent() throws ExecutionException, InterruptedException, JsonProcessingException {
        var json = """
                   {
                   	"id": null,
                   	"libraryEventType": "NEW",
                   	"book": {
                   		"id": 123,
                   		"name": "DDD",
                   		"author": "Eric"
                   	}
                   }
                """;

        kafkaTemplate.sendDefault(json).get();
        await().atMost(5, TimeUnit.SECONDS)
                .until(() ->
                        libraryEventRepository.findAll().size(), greaterThan(0)

                );

        verify(libraryEventsConsumer, times(1)).onMessage(ArgumentMatchers.any());
        verify(libraryEventService, times(1)).processLibraryEvent(any());
        assertThat(libraryEventRepository.findAll()).isNotEmpty();
        assertThat(libraryEventRepository.findAll().get(0).getLibraryEventType()).isEqualTo(LibraryEventType.NEW);
    }

    @Test
    void shouldShowErrorPublishUpdateLibraryEvent() throws ExecutionException, InterruptedException {
        var json = """
                    {
                    	"id": null,
                    	"libraryEventType": "UPDATE",
                    	"book": {
                    		"id": 123,
                    		"name": "DDD",
                    		"author": "Eric"
                    	}
                    }
                """;

        kafkaTemplate.sendDefault(json).get();
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(libraryEventService, times(3))
                        .processLibraryEvent(any()));

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(libraryEventsConsumer, times(3))
                        .onMessage(any()));

    }
}