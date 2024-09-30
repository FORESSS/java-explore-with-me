package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Validator;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final Validator validator;

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        List<Long> ids = newCompilationDto.getEvents();
        if (!CollectionUtils.isEmpty(ids)) {
            compilation.setEvents(eventRepository.findAllByIdIn(ids));
        } else {
            compilation.setEvents(Collections.emptyList());
        }
        compilationRepository.save(compilation);
        log.info("Подборка событий с id: {} создана", compilation.getId());
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = validateAndGetCompilation(compId);
        if (!CollectionUtils.isEmpty(updateCompilationDto.getEvents())) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilationDto.getEvents()));
        }
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        log.info("Подборка событий с id: {} обновлена", compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        if (!validator.isValidCompilationId(compId)) {
            throw new NotFoundException(String.format("Подборка с id: %d не найдена", compId));
        }
        compilationRepository.deleteById(compId);
        log.info("Подборка событий с id: {} удалена", compId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> find(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else if (pinned) {
            compilations = compilationRepository.findAllByPinnedTrue(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinnedFalse(pageRequest).getContent();
        }
        log.info("Получение списка подборок событий");
        return compilationMapper.toListCompilationDto(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findById(long compId) {
        Compilation compilation = validateAndGetCompilation(compId);
        log.info("Получение подборки событий с id: {}", compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    private Compilation validateAndGetCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id: %d не найдена", compId)));
    }
}