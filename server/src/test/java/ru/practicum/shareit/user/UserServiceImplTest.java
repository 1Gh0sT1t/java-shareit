package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты сервиса пользователей.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Marat", "marat@mail.ru");
    }

    @Test
    void shouldCreateUser() {
        User newUser = new User(null, "Marat", "marat@mail.ru");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(newUser)).thenReturn(user);

        User result = userService.create(newUser);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findByEmail(newUser.getEmail());
        verify(userRepository).save(newUser);
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsNullOnCreate() {
        User newUser = new User(null, "Marat", null);

        assertThrows(ValidationException.class, () -> userService.create(newUser));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsBlankOnCreate() {
        User newUser = new User(null, "Marat", " ");

        assertThrows(ValidationException.class, () -> userService.create(newUser));
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExistsOnCreate() {
        User newUser = new User(null, "Other", "marat@mail.ru");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.create(newUser));
    }

    @Test
    void shouldUpdateOnlyName() {
        User updateUser = new User(1L, "New Name", null);
        User savedUser = new User(1L, "Marat", "marat@mail.ru");
        User expectedUser = new User(1L, "New Name", "marat@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(savedUser)).thenReturn(expectedUser);

        User result = userService.update(updateUser);

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getName(), result.getName());
        assertEquals(expectedUser.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void shouldUpdateOnlyEmail() {
        User updateUser = new User(1L, null, "new@mail.ru");
        User savedUser = new User(1L, "Marat", "marat@mail.ru");
        User expectedUser = new User(1L, "Marat", "new@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.findByEmail("new@mail.ru")).thenReturn(Optional.empty());
        when(userRepository.save(savedUser)).thenReturn(expectedUser);

        User result = userService.update(updateUser);

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getName(), result.getName());
        assertEquals(expectedUser.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("new@mail.ru");
        verify(userRepository).save(savedUser);
    }

    @Test
    void shouldThrowValidationExceptionWhenIdIsNullOnUpdate() {
        User updateUser = new User(null, "New Name", null);

        assertThrows(ValidationException.class, () -> userService.update(updateUser));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundOnUpdate() {
        User updateUser = new User(99L, "New Name", null);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(updateUser));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsBlankOnUpdate() {
        User updateUser = new User(1L, null, " ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> userService.update(updateUser));
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExistsOnUpdate() {
        User updateUser = new User(1L, null, "other@mail.ru");
        User otherUser = new User(2L, "Other", "other@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("other@mail.ru")).thenReturn(Optional.of(otherUser));

        assertThrows(ConflictException.class, () -> userService.update(updateUser));
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void shouldGetAllUsers() {
        User secondUser = new User(2L, "Ivan", "ivan@mail.ru");

        when(userRepository.findAll()).thenReturn(List.of(user, secondUser));

        var result = userService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundOnDelete() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(99L));
    }
}