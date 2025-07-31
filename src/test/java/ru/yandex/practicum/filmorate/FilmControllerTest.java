package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film validFilm;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void addFilm_ValidFilm_ShouldAddFilm() {
        Film addedFilm = filmController.addFilm(validFilm);

        assertNotNull(addedFilm.getId());
        assertEquals(validFilm.getName(), addedFilm.getName());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_EmptyName_ShouldFailValidation() {
        validFilm.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void addFilm_NullName_ShouldFailValidation() {
        validFilm.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void addFilm_TooLongDescription_ShouldFailValidation() {
        String longDescription = "a".repeat(201);
        validFilm.setDescription(longDescription);

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty());
        assertEquals("Описание не должно превышать 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void addFilm_NullReleaseDate_ShouldFailValidation() {
        validFilm.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty());
        assertEquals("Дата релиза должна быть указана", violations.iterator().next().getMessage());
    }

    @Test
    void addFilm_ZeroDuration_ShouldFailValidation() {
        validFilm.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty());
        assertEquals("Продолжительность фильма должна быть положительной", violations.iterator().next().getMessage());
    }

    @Test
    void addFilm_NegativeDuration_ShouldFailValidation() {
        validFilm.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty());
        assertEquals("Продолжительность фильма должна быть положительной", violations.iterator().next().getMessage());
    }

    @Test
    void updateFilm_NonExistentId_ShouldThrowResourceNotFoundException() {
        validFilm.setId(999);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmController.updateFilm(validFilm));
        assertEquals("Фильм с ID 999 не найден", exception.getMessage());
    }
}