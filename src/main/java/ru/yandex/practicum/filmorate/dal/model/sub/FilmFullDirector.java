package ru.yandex.practicum.filmorate.dal.model.sub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmFullDirector {
    private Long filmId;
    private Long directorId;
    private String directorName;
}

