package ru.practicum.util;

import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Component
public class Validator {

    public void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new DateTimeException("Дата не указана");
        }
        if (start.isAfter(end)) {
            throw new DateTimeException("Не корректная дата");
        }
    }
}