package com.example.producer.controller;

import com.example.producer.exception.LibraryEventIdCannotBeNullException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class LibraryEventControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRequestBody(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult().getFieldErrors();
        return errors.stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
    }

    @ExceptionHandler(LibraryEventIdCannotBeNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleLibraryEventId(LibraryEventIdCannotBeNullException exception) {
        return exception.getMessage();
    }

}
