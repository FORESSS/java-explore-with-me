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
import ru.practicum.exception.NotFoundException;

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

    @Override
    public CompilationDto addCompilation(RequestCompilationDto requestCompilationDto) {
        log.info("The beginning of the process of creating a compilation");
        Compilation compilation = compilationMapper.toCompilation(requestCompilationDto);
        List<Long> ids = requestCompilationDto.getEvents();

        if (!CollectionUtils.isEmpty(ids)) {
            compilation.setEvents(eventRepository.findAllByIdIn(ids));
        } else {
            compilation.setEvents(Collections.emptyList());
        }

        Compilation createdCompilation = compilationRepository.save(compilation);
        log.info("The compilation has been created");
        return compilationMapper.toCompilationDto(createdCompilation);
    }

    @Override
    public CompilationDto updateCompilation(long compId, RequestCompilationDto requestCompilationDto) {
        log.info("The beginning of the process of updating a compilation");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id " + compId + " not found"));

        if (!CollectionUtils.isEmpty(requestCompilationDto.getEvents())) {
            compilation.setEvents(eventRepository.findAllByIdIn(requestCompilationDto.getEvents()));
        }

        if (requestCompilationDto.getPinned() != null) compilation.setPinned(requestCompilationDto.getPinned());

        if (requestCompilationDto.getTitle() != null) compilation.setTitle(requestCompilationDto.getTitle());

        log.info("The compilation has been updated");
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        log.info("The beginning of the process of deleting a compilation");
        compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id " + compId + " not found"));
        compilationRepository.deleteById(compId);
        log.info("The compilation has been deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        log.info("The beginning of the process of finding a all compilations");
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

        log.info("The all compilations has been found");
        return compilationsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        log.info("The beginning of the process of finding a all compilations by id");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id " + compId + " not found"));
        log.info("The all compilations by id has been found");
        return compilationMapper.toCompilationDto(compilation);
    }
}
