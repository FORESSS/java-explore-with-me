package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.util.Validator;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final Validator validator;

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
        log.info("Получение всех подборок событий");
        return compilationsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = validator.validateAndGetCompilation(compId);
        log.info("Получение подборки событий с id: {}", compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        List<Long> ids = compilationDto.getEvents();
        if (!CollectionUtils.isEmpty(ids)) {
            compilation.setEvents(eventRepository.findAllByIdIn(ids));
        } else {
            compilation.setEvents(Collections.emptyList());
        }
        Compilation createdCompilation = compilationRepository.save(compilation);
        log.info("Подборка с id: {} создана", compilation.getId());
        return compilationMapper.toCompilationDto(createdCompilation);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest request) {
        Compilation compilation = validator.validateAndGetCompilation(compId);
        if (!CollectionUtils.isEmpty(request.getEvents())) {
            compilation.setEvents(eventRepository.findAllByIdIn(request.getEvents()));
        }
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        log.info("Подборка с id: {} обновлена", compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        validator.validateAndGetCompilation(compId);
        compilationRepository.deleteById(compId);
        log.info("Подборка с id: {} удалена", compId);
    }
}