package ru.practicum.stat_util;

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
}