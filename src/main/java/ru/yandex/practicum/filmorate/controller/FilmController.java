package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films - Добавление нового фильма: {}", film);

        validateReleaseDate(film.getReleaseDate());

        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен. ID: {}, Название: {}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films - Обновление фильма с ID: {}", film.getId());

        validateReleaseDate(film.getReleaseDate());

        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new ResourceNotFoundException("Фильм не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлён. ID: {}, Новые данные: {}", film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET /films - Запрос всех фильмов");
        List<Film> allFilms = List.copyOf(films.values());
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