package ru.yandex.practicum.filmorate.storage.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String content;
    Long userId;
    Long filmId;
    Boolean isPositive;
    Integer useful;
}
