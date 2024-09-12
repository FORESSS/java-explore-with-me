package ru.practicum.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNotValidationData(Exception e) {
        log.error("An exception has occurred Throwable. {}", e.getMessage());
        return new ErrorResponse("INTERNAL_SERVER_ERROR","Unexpected error",
                "An unexpected error has occurred", LocalDateTime.now());
    }
}