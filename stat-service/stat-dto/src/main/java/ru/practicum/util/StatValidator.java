package ru.practicum.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.exception.DateTimeException;

import java.time.LocalDateTime;

@Component
public class StatValidator {

    public void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new DateTimeException("Дата не указана");
        }
        if (start.isAfter(end)) {
            throw new DateTimeException("Неправильная дата");
        }
    }

    public void checkResponseStatus(ResponseEntity<Void> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Ошибка при сохранении информации, код ошибки: " + response.getStatusCode());
        }
    }
}