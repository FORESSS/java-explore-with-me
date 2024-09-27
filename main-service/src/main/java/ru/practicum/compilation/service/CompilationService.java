package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.RequestCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(RequestCompilationDto compilationDto);

    CompilationDto updateCompilation(long compId, RequestCompilationDto requestCompilationDto);

    void deleteCompilation(long compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compId);
}