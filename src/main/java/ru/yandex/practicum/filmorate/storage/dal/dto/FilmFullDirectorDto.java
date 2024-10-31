package ru.yandex.practicum.filmorate.storage.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmFullDirectorDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long filmId;
    private Long directorId;
    private String directorName;
}

