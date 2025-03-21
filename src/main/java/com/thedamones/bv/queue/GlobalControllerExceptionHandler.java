package com.thedamones.bv.queue;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MessageNotFoundException.class)
    public void handleMessageNotFoundException(MessageNotFoundException ex) {
        // No return value
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MessageEnqueueException.class)
    public void handleMessageEnqueueException(MessageEnqueueException ex) {
        // No return value
    }
}
