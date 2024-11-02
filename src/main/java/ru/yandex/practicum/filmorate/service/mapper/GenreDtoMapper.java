package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.model.Genre;

import java.util.Collection;
import java.util.List;

public class GenreDtoMapper {
    public static GenreDto dtoFromModel(Genre model) {
        return GenreDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    public static Collection<GenreDto> dtoFromModel(Collection<Genre> models) {
        if (models == null) {
            return List.of();
        }

        return models.stream()
                .map(GenreDtoMapper::dtoFromModel)
                .toList();
    }
}
