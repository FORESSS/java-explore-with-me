package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getAllRequests(long userId);

    RequestDto addRequest(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);
}