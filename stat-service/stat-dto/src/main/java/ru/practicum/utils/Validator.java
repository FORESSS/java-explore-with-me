package ru.practicum.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Validator {
    public void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new DateTimeException("Дата не указана");
        }
    }
}