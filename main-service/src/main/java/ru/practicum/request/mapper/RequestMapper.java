package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.request.dto.RequestUpdateStatusDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDto requestToParticipationRequestDto(Request request);

    List<RequestDto> listRequestToListParticipationRequestDto(List<Request> request);


    default List<RequestDto> getConfirmedRequests(List<Request> request) {
        return request.stream()
                .filter(r -> r.getStatus() == Status.CONFIRMED)
                .map(this::requestToParticipationRequestDto)
                .toList();
    }


    default List<RequestDto> getRejectedRequests(List<Request> request) {
        return request.stream()
                .filter(r -> r.getStatus() == Status.REJECTED)
                .map(this::requestToParticipationRequestDto)
                .toList();
    }

    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(requests))")
    @Mapping(target = "rejectedRequests", expression = "java(getRejectedRequests(requests))")
    RequestUpdateStatusDto toEventRequestStatusResult(Integer dummy, List<Request> requests);
}
