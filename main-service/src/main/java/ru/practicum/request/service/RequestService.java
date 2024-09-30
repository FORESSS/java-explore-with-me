package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> find(long userId);

    RequestDto add(long userId, long eventId);

    RequestDto cancel(long userId, long requestId);
}