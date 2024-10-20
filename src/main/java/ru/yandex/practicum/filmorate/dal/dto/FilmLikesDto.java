package ru.yandex.practicum.filmorate.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmLikesDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long filmId;
    private Long userId;
}
