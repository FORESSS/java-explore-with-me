package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.EndpointHitDto;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
}
