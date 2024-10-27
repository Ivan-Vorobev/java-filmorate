package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.storage.dal.dto.DirectorDto;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<DirectorDto> findAll();

    Optional<DirectorDto> findById(Long directorId);

    DirectorDto add(DirectorDto directorDto);

    DirectorDto update(DirectorDto directorDto);

    void delete(DirectorDto directorDto);

    void add_film(Long filmId, Long directorId);

    void delete_by_film(Long filmId);
}
