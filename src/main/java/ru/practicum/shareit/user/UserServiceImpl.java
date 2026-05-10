package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

/**
 * Сервис для работы с пользователями.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        validateEmailForCreate(user);
        checkEmailUnique(user.getEmail(), null);

        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Идентификатор пользователя не указан");
        }

        User savedUser = getUserOrThrow(user.getId());

        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            if (user.getEmail().isBlank()) {
                throw new ValidationException("Электронная почта не должна быть пустой");
            }

            checkEmailUnique(user.getEmail(), savedUser.getId());
            savedUser.setEmail(user.getEmail());
        }

        return userRepository.save(savedUser);
    }

    @Override
    public User getById(Long userId) {
        return getUserOrThrow(userId);
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long userId) {
        User user = getUserOrThrow(userId);
        userRepository.delete(user);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    private void validateEmailForCreate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не должна быть пустой");
        }
    }

    private void checkEmailUnique(String email, Long userId) {
        User userWithEmail = userRepository.findByEmail(email)
                .orElse(null);

        if (userWithEmail != null && !userWithEmail.getId().equals(userId)) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }
}