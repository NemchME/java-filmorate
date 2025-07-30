package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть указана")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}