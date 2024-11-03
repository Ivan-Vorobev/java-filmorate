package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.model.Genre;
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.mapper.GenreDtoMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<GenreDto> findAll() {
        return GenreDtoMapper.dtoFromModel(genreStorage.findAll());
    }

    public GenreDto findGenreById(Long genreId) {
        Genre genre = genreStorage.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found. Id: " + genreId));

        return GenreDtoMapper.dtoFromModel(genre);
    }
}
