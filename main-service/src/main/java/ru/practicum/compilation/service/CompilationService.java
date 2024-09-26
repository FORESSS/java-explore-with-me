package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compId);

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    void deleteCompilation(long compId);
}