package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto> toListCompilationDto(List<Compilation> compilations);
}