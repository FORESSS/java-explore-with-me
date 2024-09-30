package ru.practicum.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByEmailTest() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("User");
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findUserByEmail("test@test.ru");

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.get().getEmail());
        assertEquals(user.getName(), foundUser.get().getName());
    }

    @Test
    void findAllByIdInTest() {
        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setName("User 1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@test.ru");
        user2.setName("User 2");
        userRepository.save(user2);

        List<Long> ids = List.of(user1.getId(), user2.getId());
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> users = userRepository.findAllByIdIn(ids, pageRequest);

        assertNotNull(users);
        assertEquals(2, users.getContent().size());
        assertEquals(user1.getEmail(), users.getContent().get(0).getEmail());
        assertEquals(user2.getEmail(), users.getContent().get(1).getEmail());
    }
}