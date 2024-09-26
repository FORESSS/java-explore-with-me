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
    @ExceptionHandler({DateTimeException.class, MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ErrorResponse handleBadRequest(Exception ex) {
        log.error(ex.getMessage());
        return new ErrorResponse("Некорректный запрос", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler( RestrictionsViolationException.class)
    public ErrorResponse handleConflict(RestrictionsViolationException ex) {
        log.error(ex.getMessage());
        return new ErrorResponse("Конфликт", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        log.error(ex.getMessage());
        return new ErrorResponse("Не найдено", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleInternalServerError(Exception ex) {
        log.error(ex.getMessage());
        return new ErrorResponse("Ошибка", ex.getMessage());
    }
}