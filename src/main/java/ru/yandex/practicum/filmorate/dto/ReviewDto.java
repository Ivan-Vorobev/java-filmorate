package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {
    Long reviewId;
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    Integer useful;
}
