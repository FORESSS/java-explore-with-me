package ru.practicum.EndpointHit.mapper;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHit.model.EndpointHit;
import ru.practicum.EndpointHitDto;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto);

    EndpointHitDto endpointHitToEndpointHitDto(EndpointHit endpointHit);
}
