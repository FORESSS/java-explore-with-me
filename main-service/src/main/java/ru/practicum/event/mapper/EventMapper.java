package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "category", expression = "java(null)")
    Event toEvent(NewEventDto newEventDto);

    EventFullDto toEventFullDto(Event event);

    List<EventShortDto> toListEventShortDto(List<Event> events);

    List<EventFullDto> toListEventFullDto(List<Event> events);
}