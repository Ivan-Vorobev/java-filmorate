package ru.yandex.practicum.filmorate.storage.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Long ratingId;
    private String name;
    private String description;
    private Collection<GenreDto> genres;
    private Long genreId;
    private String genreName;
    private String ratingName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate releaseDate;
    private Integer duration;
}
