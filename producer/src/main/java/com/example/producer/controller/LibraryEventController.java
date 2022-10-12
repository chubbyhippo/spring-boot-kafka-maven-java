package com.example.producer.controller;

import com.example.producer.domain.LibraryEvent;
import com.example.producer.domain.LibraryEventType;
import com.example.producer.exception.LibraryEventIdCannotBeNullException;
import com.example.producer.service.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class LibraryEventController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryEvent postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent(libraryEvent);
        return libraryEvent;
    }

    @PutMapping("/v1/libraryevent")
    @ResponseStatus(HttpStatus.OK)
    public LibraryEvent putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException,
            LibraryEventIdCannotBeNullException {
        if (libraryEvent.getId() == null) {
            throw new LibraryEventIdCannotBeNullException("LibraryEvent id cannot be null");
        }
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEvent(libraryEvent);
        return libraryEvent;
    }
}
