package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.dto.DirectorDto;

public class DirectorMapper {
    public static Director modelFromDto(DirectorDto dto) {
        return Director.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static DirectorDto dtoFromModel(Director model) {
        return DirectorDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}
