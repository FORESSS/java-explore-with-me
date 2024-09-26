package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ViewStatsMapper {
    List<ViewStatsDto> toListViewStatsDto(List<ViewStats> viewStats);
}