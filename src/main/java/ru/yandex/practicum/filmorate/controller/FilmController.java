package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films - Добавление нового фильма: {}", film);
        validateReleaseDate(film.getReleaseDate());
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Фильм успешно добавлен. ID: {}, Название: {}", addedFilm.getId(), addedFilm.getName());
        return addedFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films - Обновление фильма с ID: {}", film.getId());
        validateReleaseDate(film.getReleaseDate());

        if (filmStorage.getFilm(film.getId()) == null) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new ResourceNotFoundException("Фильм не найден");
        }

        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Фильм успешно обновлён. ID: {}, Новые данные: {}", updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET /films - Запрос всех фильмов");
        List<Film> allFilms = filmStorage.getAllFilms();
        log.debug("Найдено {} фильмов", allFilms.size());
        return allFilms;
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза {} раньше допустимой {}", releaseDate, MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}