package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты репозитория пользователей.
 */
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User(null, "Marat", "marat@mail.ru");
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("marat@mail.ru");

        assertTrue(result.isPresent());
        assertEquals(user.getName(), result.get().getName());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> result = userRepository.findByEmail("unknown@mail.ru");

        assertTrue(result.isEmpty());
    }
}