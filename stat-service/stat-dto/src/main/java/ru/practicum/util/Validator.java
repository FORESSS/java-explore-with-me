package ru.practicum.util;

import org.springframework.stereotype.Component;
import ru.practicum.exception.DateTimeException;

import java.time.LocalDateTime;

@Component
public class Validator {
    public void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new DateTimeException("Не корректная дата");
        }
    }
}