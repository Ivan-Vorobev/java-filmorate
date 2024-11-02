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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<GenreDto> findAll() {
        return genreStorage.findAll().stream()
                .map(GenreDtoMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public GenreDto findGenre(Long genreId) {
        Genre genre = genreStorage.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found. Id: " + genreId));

        return GenreDtoMapper.modelFromDto(genre);
    }

    public Map<Long, GenreDto> findAllAsMap() {
        HashMap<Long, GenreDto> genresMap = new HashMap<>();
        for (Genre genre : genreStorage.findAll()) {
            genresMap.put(genre.getId(), GenreDtoMapper.modelFromDto(genre));
        }

        return genresMap;
    }
}
