package ru.yandex.practicum.filmorate.dal.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Long id;
    String content;
    Long userId;
    Long filmId;
    Boolean isPositive;
    Integer useful;
}
