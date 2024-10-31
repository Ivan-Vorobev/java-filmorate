package ru.yandex.practicum.filmorate.storage.dal.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class FilmFullGenreDto extends FilmGenreDto {
    private String genreName;
}

