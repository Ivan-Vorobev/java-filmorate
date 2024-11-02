package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dal.model.Director;
import ru.yandex.practicum.filmorate.dal.model.Film;
import ru.yandex.practicum.filmorate.dal.model.Genre;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.ArrayList;
import java.util.Collection;

public class FilmDtoMapper {
    public static FilmDto modelFromDto(Film dto) {
        Collection<Genre> genres = dto.getGenres();
        if (genres == null) {
            genres = new ArrayList<>();
        }

        Collection<Director> directors = dto.getDirectors();
        if (directors == null) {
            directors = new ArrayList<>();
        }

        return FilmDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .mpa(RatingDto.builder().id(dto.getRatingId()).name(dto.getRatingName()).build())
                .genres(
                        genres.stream()
                                .map(GenreDtoMapper::modelFromDto)
                                .toList()
                )
                .directors(directors.stream()
                        .map(DirectorDtoMapper::modelFromDto)
                        .toList())
                .duration(dto.getDuration())
                .build();
    }

    public static Film dtoFromModel(FilmDto model) {
        return Film.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .releaseDate(model.getReleaseDate())
                .ratingId(model.getMpa() != null ? model.getMpa().getId() : null)
                .ratingName(model.getMpa() != null ? model.getMpa().getName() : null)
                .duration(model.getDuration())
                .build();
    }
}
