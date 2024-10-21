package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.findAll().stream()
                .map(GenreMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public Genre findGenre(Long genreId) {
        GenreDto genre = genreStorage.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found. Id: " + genreId));

        return GenreMapper.modelFromDto(genre);
    }

    public Map<Long, Genre> findAllAsMap() {
        HashMap<Long, Genre> genresMap = new HashMap<>();
        for (GenreDto genre : genreStorage.findAll()) {
            genresMap.put(genre.getId(), GenreMapper.modelFromDto(genre));
        }

        return genresMap;
    }
}
