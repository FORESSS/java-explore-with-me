package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.ViewStats;
import ru.practicum.ViewStatsDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    List<ViewStatsDto> toListViewStatsDto(List<ViewStats> viewStats);
}
