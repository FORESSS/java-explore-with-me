package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDto toRequestDto(Request request);

    List<RequestDto> toListRequestDto(List<Request> request);

    default List<RequestDto> getConfirmedRequests(List<Request> requests) {
        return requests.stream()
                .filter(r -> r.getStatus() == Status.CONFIRMED)
                .map(this::toRequestDto)
                .toList();
    }

    default List<RequestDto> getRejectedRequests(List<Request> requests) {
        return requests.stream()
                .filter(r -> r.getStatus() == Status.REJECTED)
                .map(this::toRequestDto)
                .toList();
    }

    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(requests))")
    @Mapping(target = "rejectedRequests", expression = "java(getRejectedRequests(requests))")
    RequestStatusDto toRequestStatusDto(Integer dummy, List<Request> requests);
}