package ru.practicum.compilation.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.compilation.model.Compilation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CompilationRepositoryTest {
    @Autowired
    private CompilationRepository compilationRepository;
    private Compilation compilation1;
    private Compilation compilation2;

    @BeforeEach
    void setUp() {
        compilation1 = new Compilation();
        compilation1.setPinned(true);
        compilation1.setTitle("Test Compilation 1");
        compilationRepository.save(compilation1);

        compilation2 = new Compilation();
        compilation2.setPinned(false);
        compilation2.setTitle("Test Compilation 2");
        compilationRepository.save(compilation2);
    }

    @Test
    void findAllByPinnedTrueTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Compilation> compilations = compilationRepository.findAllByPinnedTrue(pageRequest);

        assertNotNull(compilations);
        assertEquals(1, compilations.getTotalElements());
        assertEquals(true, compilations.getContent().get(0).getPinned());
    }

    @Test
    void findAllByPinnedFalseTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Compilation> compilations = compilationRepository.findAllByPinnedFalse(pageRequest);

        assertNotNull(compilations);
        assertEquals(1, compilations.getTotalElements());
        assertEquals(false, compilations.getContent().get(0).getPinned());
    }
}