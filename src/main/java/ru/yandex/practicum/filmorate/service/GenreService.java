package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dal.model.Genre;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.mapper.GenreDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    public Map<Long, GenreDto> findAllAsMap() {
        HashMap<Long, GenreDto> genresMap = new HashMap<>();
        for (Genre genre : genreStorage.findAll()) {
            genresMap.put(genre.getId(), GenreDtoMapper.dtoFromModel(genre));
        }

        return genresMap;
    }
}
