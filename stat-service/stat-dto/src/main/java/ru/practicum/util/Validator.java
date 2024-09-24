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
    }

    public void checkDateTime(String start, String end) {
        if (parseTime(start).isAfter(parseTime(end))) {
            throw new DateTimeException("Не корректно указана дата");
        }
    }

    public LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time, Constants.FORMATTER);
    }
}