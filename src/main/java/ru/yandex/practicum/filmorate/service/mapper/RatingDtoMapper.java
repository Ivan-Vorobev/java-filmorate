package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dal.model.Rating;

public class RatingDtoMapper {
    public static RatingDto modelFromDto(Rating dto) {
        return RatingDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
