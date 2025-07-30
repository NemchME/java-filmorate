package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User validUser;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setLogin("validLogin");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void createUser_ValidUser_ShouldCreateUser() {
        User createdUser = userController.createUser(validUser);

        assertNotNull(createdUser.getId());
        assertEquals(validUser.getLogin(), createdUser.getLogin());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void createUser_EmptyLogin_ShouldFailValidation() {
        validUser.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Логин не может быть пустым")));
    }

    @Test
    void createUser_NullLogin_ShouldFailValidation() {
        validUser.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Логин не может быть пустым")));
    }

    @Test
    void createUser_LoginWithSpaces_ShouldFailValidation() {
        validUser.setLogin("login with spaces");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Логин не должен содержать пробелы")));
    }

    @Test
    void createUser_EmptyName_ShouldUseLoginAsName() {
        validUser.setName("");

        User createdUser = userController.createUser(validUser);
        assertEquals(validUser.getLogin(), createdUser.getName());
    }

    @Test
    void createUser_NullName_ShouldUseLoginAsName() {
        validUser.setName(null);

        User createdUser = userController.createUser(validUser);
        assertEquals(validUser.getLogin(), createdUser.getName());
    }

    @Test
    void createUser_InvalidEmail_ShouldFailValidation() {
        validUser.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Электронная почта должна быть корректного формата")));
    }

    @Test
    void createUser_NullEmail_ShouldFailValidation() {
        validUser.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Электронная почта не может быть пустой")));
    }

    @Test
    void createUser_FutureBirthday_ShouldFailValidation() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Дата рождения не может быть в будущем")));
    }

    @Test
    void updateUser_NonExistentId_ShouldThrowResourceNotFoundException() {
        validUser.setId(999);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userController.updateUser(validUser));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}