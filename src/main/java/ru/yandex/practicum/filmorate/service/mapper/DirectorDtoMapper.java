package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dal.model.Director;

import java.util.Collection;

public class DirectorDtoMapper {
    public static DirectorDto dtoFromModel(Director model) {
        return DirectorDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    public static Collection<DirectorDto> dtoFromModel(Collection<Director> models) {
        return models.stream()
                .map(DirectorDtoMapper::dtoFromModel)
                .toList();
    }

    public static Director modelFromDto(DirectorDto dto) {
        return Director.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
