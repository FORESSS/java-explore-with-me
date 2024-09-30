package ru.practicum.event.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EntityManager entityManager;
    private User initiator;
    private Event event;
    private Category category;
    private Location location;

    @BeforeEach
    void setUp() {
        initiator = new User();
        initiator.setName("User");
        initiator.setEmail("test@test.ru");
        entityManager.persist(initiator);

        category = new Category();
        category.setName("Category");
        entityManager.persist(category);

        location = new Location();
        location.setLat(56.0f);
        location.setLon(38.0f);
        entityManager.persist(location);

        event = new Event();
        event.setAnnotation("Annotation");
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setDescription("Description");
        event.setTitle("Title");
        event.setPaid(false);
        event.setParticipantLimit(10L);
        event.setPublishedOn(LocalDateTime.now());
        event.setRequestModeration(true);
        event.setState(State.PUBLISHED);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setLocation(location);
        entityManager.persist(event);
    }

    @Test
    void findByIdAndStateTest() {
        Optional<Event> foundEvent = eventRepository.findByIdAndState(event.getId(), State.PUBLISHED);

        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getId()).isEqualTo(event.getId());
        assertThat(foundEvent.get().getState()).isEqualTo(State.PUBLISHED);

        foundEvent = eventRepository.findByIdAndState(event.getId(), State.CANCELED);

        assertThat(foundEvent).isNotPresent();
    }

    @Test
    void findAllByIdInTest() {
        List<Long> ids = List.of(event.getId());
        List<Event> events = eventRepository.findAllByIdIn(ids);

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getId()).isEqualTo(event.getId());
    }

    @Test
    void findAllByCategoryIdTest() {
        List<Event> events = eventRepository.findAllByCategoryId(category.getId());

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    void findByInitiatorIdAndIdTest() {
        Optional<Event> foundEvent = eventRepository.findByInitiatorIdAndId(initiator.getId(), event.getId());

        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getId()).isEqualTo(event.getId());
        assertThat(foundEvent.get().getInitiator().getId()).isEqualTo(initiator.getId());

        foundEvent = eventRepository.findByInitiatorIdAndId(999L, event.getId());

        assertThat(foundEvent).isNotPresent();
    }
}