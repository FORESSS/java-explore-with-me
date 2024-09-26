package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toParticipationRequestDto(Request request);

    List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> request);

    default List<ParticipationRequestDto> getConfirmedRequests(List<Request> request) {
        return request.stream()
                .filter(r -> r.getStatus() == Status.CONFIRMED)
                .map(this::toParticipationRequestDto)
                .toList();
    }

    default List<ParticipationRequestDto> getRejectedRequests(List<Request> request) {
        return request.stream()
                .filter(r -> r.getStatus() == Status.REJECTED)
                .map(this::toParticipationRequestDto)
                .toList();
    }

    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(requests))")
    @Mapping(target = "rejectedRequests", expression = "java(getRejectedRequests(requests))")
    RequestUpdateResultDto toEventRequestStatusResult(Integer dummy, List<Request> requests);
}