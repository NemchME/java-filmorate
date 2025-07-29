package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("POST /users - Создание нового пользователя. Данные: {}", user);
        try {
            validateUser(user);

            if (user.getName() == null || user.getName().isBlank()) {
                log.debug("Имя пользователя пустое, используем логин: {}", user.getLogin());
                user.setName(user.getLogin());
            }

            user.setId(nextId++);
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан. ID: {}, Логин: {}", user.getId(), user.getLogin());
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка создания пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("PUT /users - Обновление пользователя с ID: {}", user.getId());
        try {
            if (!users.containsKey(user.getId())) {
                log.warn("Пользователь с ID {} не найден", user.getId());
                throw new ValidationException("Пользователь не найден");
            }

            validateUser(user);
            users.put(user.getId(), user);
            log.info("Пользователь успешно обновлён. ID: {}, Новые данные: {}", user.getId(), user);
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка обновления пользователя ID {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("GET /users - Запрос списка всех пользователей");
        List<User> allUsers = List.copyOf(users.values());
        log.debug("Найдено {} пользователей", allUsers.size());
        return allUsers;
    }

    private void validateUser(User user) {
        log.debug("Валидация пользователя: {}", user);

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Некорректный email: {}", user.getEmail());
            throw new ValidationException("Электронная почта должна содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Пустой логин");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Логин содержит пробелы: '{}'", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        log.debug("Валидация пользователя пройдена успешно");
    }
}