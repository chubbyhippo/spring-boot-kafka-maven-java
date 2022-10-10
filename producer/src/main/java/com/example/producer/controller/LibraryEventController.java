package com.example.producer.controller;

import com.example.producer.domain.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryEventController {

    @PostMapping("/v1/libraryevent")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryEvent postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {
        return libraryEvent;
    }
}
