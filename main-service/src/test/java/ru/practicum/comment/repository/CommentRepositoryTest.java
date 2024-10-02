package ru.practicum.comment.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.category.model.Category;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager entityManager;
    private User author;
    private Event event;
    private Comment comment;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setName("User");
        author.setEmail("test@test.ru");
        entityManager.persist(author);

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
        event.setInitiator(author);
        event.setCategory(category);
        event.setLocation(location);
        entityManager.persist(event);

        comment = new Comment();
        comment.setText("Comment");
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        entityManager.persist(comment);
    }

    @Test
    void findAllByEventIdTest() {
        Page<Comment> comments = commentRepository.findAllByEventId(1L, PageRequest.of(0, 10));

        assertNotNull(comments);
        assertEquals(1, comments.getTotalElements());
    }

    @Test
    void findAllByAuthorIdTest() {
        Page<Comment> comments = commentRepository.findAllByAuthorId(2L, PageRequest.of(0, 10));

        assertNotNull(comments);
        assertEquals(1, comments.getTotalElements());
    }
}