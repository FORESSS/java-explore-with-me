package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.RequestCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
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
    public CompilationDto addCompilation(RequestCompilationDto requestCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(requestCompilationDto);
        List<Long> ids = requestCompilationDto.getEvents();
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
    public CompilationDto updateCompilation(long compId, RequestCompilationDto requestCompilationDto) {
        Compilation compilation = validator.validateAndGetCompilation(compId);
        if (!CollectionUtils.isEmpty(requestCompilationDto.getEvents())) {
            compilation.setEvents(eventRepository.findAllByIdIn(requestCompilationDto.getEvents()));
        }
        if (requestCompilationDto.getPinned() != null) {
            compilation.setPinned(requestCompilationDto.getPinned());
        }
        if (requestCompilationDto.getTitle() != null) {
            compilation.setTitle(requestCompilationDto.getTitle());
        }
        log.info("Подборка событий с id: {} обновлена", compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        validator.checkCompilationId(compId);
        compilationRepository.deleteById(compId);
        log.info("Подборка событий с id: {} удалена", compId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<CompilationDto> compilationsDto;
        if (pinned == null) {
            compilationsDto = compilationMapper.toListCompilationDto(compilationRepository
                    .findAll(pageRequest).getContent());
        } else if (pinned) {
            compilationsDto = compilationMapper.toListCompilationDto(
                    compilationRepository.findAllByPinnedTrue(pageRequest).getContent());
        } else {
            compilationsDto = compilationMapper.toListCompilationDto(
                    compilationRepository.findAllByPinnedFalse(pageRequest).getContent());
        }
        log.info("Получение списка подборок событий");
        return compilationsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = validator.validateAndGetCompilation(compId);
        log.info("Получение подборки событий с id: {}", compId);
        return compilationMapper.toCompilationDto(compilation);
    }
}