package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.model.Genre;

public class GenreDtoMapper {
    public static GenreDto modelFromDto(Genre dto) {
        return GenreDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
