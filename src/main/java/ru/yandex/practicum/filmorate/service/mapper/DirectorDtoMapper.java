package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dal.model.Director;

public class DirectorDtoMapper {
    public static DirectorDto modelFromDto(Director dto) {
        return DirectorDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static Director dtoFromModel(DirectorDto model) {
        return Director.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}
