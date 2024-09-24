package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Validator {
    public void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new DateTimeException("Не указана дата");
        }
        if (start.isAfter(end)) {
            throw new DateTimeException("Не корректно указана дата");
        }
    }
}