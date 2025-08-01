package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film validFilm;
    private Validator validator;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage());
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userService));

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
        ResponseEntity<Film> response = filmController.createFilm(validFilm);
        Film addedFilm = response.getBody();

        assertNotNull(addedFilm);
        assertNotNull(addedFilm.getId());
        assertEquals(validFilm.getName(), addedFilm.getName());

        ResponseEntity<List<Film>> allFilmsResponse = filmController.getAllFilms();
        assertEquals(1, allFilmsResponse.getBody().size());
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
        Film filmToUpdate = new Film();
        filmToUpdate.setId(999);
        filmToUpdate.setName("Non-existent film");
        filmToUpdate.setDescription("Description");
        filmToUpdate.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmToUpdate.setDuration(120);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmController.updateFilm(filmToUpdate));
        assertEquals("Фильм с ID 999 не найден", exception.getMessage());
    }

    @Test
    void getFilm_NonExistentId_ShouldThrowResourceNotFoundException() {
        int nonExistentId = 999;
        assertThrows(ResourceNotFoundException.class,
                () -> filmController.getFilm(nonExistentId));
    }

    @Test
    void deleteFilm_ShouldRemoveFilm() {
        ResponseEntity<Film> response = filmController.createFilm(validFilm);
        Film addedFilm = response.getBody();
        int filmId = addedFilm.getId();

        ResponseEntity<Void> deleteResponse = filmController.deleteFilm(filmId);
        assertEquals(204, deleteResponse.getStatusCodeValue());

        assertThrows(ResourceNotFoundException.class,
                () -> filmController.getFilm(filmId));
    }

    @Test
    void getAllFilms_ShouldReturnAllFilms() {
        filmController.createFilm(validFilm);

        Film anotherFilm = new Film();
        anotherFilm.setName("Another Film");
        anotherFilm.setDescription("Another description");
        anotherFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        anotherFilm.setDuration(90);
        filmController.createFilm(anotherFilm);

        ResponseEntity<List<Film>> response = filmController.getAllFilms();
        assertEquals(2, response.getBody().size());
    }
}