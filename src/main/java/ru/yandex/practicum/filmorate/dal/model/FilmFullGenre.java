package ru.yandex.practicum.filmorate.dal.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class FilmFullGenre extends FilmGenre {
    private String genreName;
}

