package ru.yandex.practicum.filmorate.dal.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
public class Film {
    private Long id;
    private Long ratingId;
    private String name;
    private String description;
    private Collection<Genre> genres;
    private Collection<Director> directors;
    private String genreName;
    private String directorName;
    private String ratingName;
    private LocalDate releaseDate;
    private Integer duration;
}
