package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dal.model.Director;
import ru.yandex.practicum.filmorate.dal.model.Film;
import ru.yandex.practicum.filmorate.dal.model.Genre;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilmDtoMapper {
    public static FilmDto dtoFromModel(Film model) {
        Collection<Genre> genres = model.getGenres();
        if (genres == null) {
            genres = new ArrayList<>();
        }

        Collection<Director> directors = model.getDirectors();
        if (directors == null) {
            directors = new ArrayList<>();
        }

        return FilmDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .releaseDate(model.getReleaseDate())
                .mpa(RatingDto.builder().id(model.getRatingId()).name(model.getRatingName()).build())
                .genres(GenreDtoMapper.dtoFromModel(genres))
                .directors(DirectorDtoMapper.dtoFromModel(directors))
                .duration(model.getDuration())
                .build();
    }

    public static Collection<FilmDto> dtoFromModel(Collection<Film> models) {
        if (models == null) {
            return List.of();
        }

        return models.stream()
                .map(FilmDtoMapper::dtoFromModel)
                .toList();
    }

    public static Film modelFromDto(FilmDto dto) {
        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .ratingId(dto.getMpa() != null ? dto.getMpa().getId() : null)
                .ratingName(dto.getMpa() != null ? dto.getMpa().getName() : null)
                .duration(dto.getDuration())
                .build();
    }
}
