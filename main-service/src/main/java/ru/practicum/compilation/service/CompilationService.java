package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto add(NewCompilationDto newCompilationDto);

    CompilationDto update(long compId, UpdateCompilationDto updateCompilationDto);

    void delete(long compId);

    List<CompilationDto> find(Boolean pinned, int from, int size);

    CompilationDto findById(long compId);
}