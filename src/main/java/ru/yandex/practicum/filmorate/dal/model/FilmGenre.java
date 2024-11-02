package ru.yandex.practicum.filmorate.dal.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class FilmGenre {
    private Long filmId;
    private Long genreId;
}
