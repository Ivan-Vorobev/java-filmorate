package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dal.model.Rating;

import java.util.Collection;
import java.util.List;

public class RatingDtoMapper {
    public static RatingDto dtoFromModel(Rating model) {
        return RatingDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    public static Collection<RatingDto> dtoFromModel(Collection<Rating> models) {
        if (models == null) {
            return List.of();
        }

        return models.stream()
                .map(RatingDtoMapper::dtoFromModel)
                .toList();
    }
}
