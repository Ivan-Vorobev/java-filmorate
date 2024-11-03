package ru.yandex.practicum.filmorate.dal.model.sub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenre {
    private Long filmId;
    private Long genreId;
}
