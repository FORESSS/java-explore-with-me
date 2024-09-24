package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DateTimeException.class, MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ErrorResponse handleBadRequest(Exception ex) {
        log.error(ex.getMessage());
        return new ErrorResponse("Ошибка111", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleCommonException(Exception ex) {
        return new ErrorResponse("Ошибка2222", ex.getMessage());
    }
}