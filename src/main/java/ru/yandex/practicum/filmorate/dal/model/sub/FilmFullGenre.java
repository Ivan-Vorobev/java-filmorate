package ru.yandex.practicum.filmorate.dal.model.sub;

import lombok.*;

@Data
@Builder
public class FilmFullGenre {
    private Long filmId;
    private Long genreId;
    private String genreName;
}

