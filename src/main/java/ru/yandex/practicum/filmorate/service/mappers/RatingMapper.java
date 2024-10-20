package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.dal.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

public class RatingMapper {
    public static Rating modelFromDto(RatingDto dto) {
        return Rating.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
