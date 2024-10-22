package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.Collection;

public class FilmMapper {
    public static Film modelFromDto(FilmDto dto) {
        Collection<GenreDto> genres = dto.getGenres();
        if (genres == null) {
            genres = new ArrayList<>();
        }

        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .mpa(Rating.builder().id(dto.getRatingId()).name(dto.getRatingName()).build())
                .genres(
                        genres.stream()
                                .map(GenreMapper::modelFromDto)
                                .toList()
                )
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
                .ratingName(model.getMpa() != null ? model.getMpa().getName() : null)
                .duration(model.getDuration())
                .build();
    }
}
