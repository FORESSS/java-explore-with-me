package ru.practicum.request.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RequestsRepositoryTest {
    @Autowired
    private RequestsRepository requestsRepository;
    @Autowired
    private EntityManager entityManager;
    private User requester;
    private Event event;
    private Request request;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("User");
        requester.setEmail("test@test.ru");
        entityManager.persist(requester);

        Category category = new Category();
        category.setName("Category");
        entityManager.persist(category);

        Location location = new Location();
        location.setLat(56.0f);
        location.setLon(38.0f);
        entityManager.persist(location);

        event = new Event();
        event.setAnnotation("Event");
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setDescription("Description");
        event.setTitle("Title");
        event.setPaid(false);
        event.setParticipantLimit(10L);
        event.setPublishedOn(LocalDateTime.now());
        event.setRequestModeration(true);
        event.setState(State.PUBLISHED);
        event.setInitiator(requester);
        event.setCategory(category);
        event.setLocation(location);
        entityManager.persist(event);

        request = new Request();
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request.setStatus(Status.PENDING);
        entityManager.persist(request);
    }

    @Test
    void findByEventIdTest() {
        List<Request> requests = requestsRepository.findByEventId(event.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getId()).isEqualTo(request.getId());
        assertThat(requests.get(0).getEvent().getId()).isEqualTo(event.getId());
    }

    @Test
    void findByIdInTest() {
        Set<Long> ids = Set.of(request.getId());
        List<Request> requests = requestsRepository.findByIdIn(ids);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getId()).isEqualTo(request.getId());
    }

    @Test
    void findAllByRequesterIdTest() {
        List<Request> requests = requestsRepository.findAllByRequesterId(requester.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getRequester().getId()).isEqualTo(requester.getId());
    }

    @Test
    void findByRequesterIdAndEventIdTest() {
        Optional<Request> foundRequest = requestsRepository.findByRequesterIdAndEventId(requester.getId(), event.getId());

        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get().getId()).isEqualTo(request.getId());
    }

    @Test
    void findAllByEventIdAndStatusTest() {
        List<Request> requests = requestsRepository.findAllByEventIdAndStatus(event.getId(), Status.PENDING);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getStatus()).isEqualTo(Status.PENDING);
    }
}