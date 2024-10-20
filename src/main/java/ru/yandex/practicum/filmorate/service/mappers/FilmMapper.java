package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

public class FilmMapper {
    public static Film modelFromDto(FilmDto dto) {
        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .mpa(Rating.builder().id(dto.getRatingId()).build())
                .duration(dto.getDuration())
                .build();
    }

    public static FilmDto dtoFromModel(Film model) {
        return FilmDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .releaseDate(model.getReleaseDate())
                .ratingId(model.getMpa() != null ? model.getMpa().getId() : null)
                .duration(model.getDuration())
                .build();
    }
}
