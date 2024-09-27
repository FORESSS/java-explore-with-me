package ru.practicum.stat_util;

import lombok.experimental.UtilityClass;
import ru.practicum.exception.DateTimeException;

import java.time.LocalDateTime;

@UtilityClass
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