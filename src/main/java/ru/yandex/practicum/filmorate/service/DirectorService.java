package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.dal.dto.DirectorDto;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> findAll() {
        return directorStorage.findAll().stream()
                .map(DirectorMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public Director findDirectorByID(Long directorId) {
        DirectorDto director = directorStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found. Id: " + directorId));

        return DirectorMapper.modelFromDto(director);
    }

    public Director create(Director director) {
        return DirectorMapper.modelFromDto(
                directorStorage.add(
                        DirectorMapper.dtoFromModel(director)
                )
        );
    }

    public Director update(Director director) {
        findDirectorByID(director.getId());
        return DirectorMapper.modelFromDto(
                directorStorage.update(
                        DirectorMapper.dtoFromModel(director)
                )
        );
    }

    public void delete(Long direcorId) {
        Director director = findDirectorByID(direcorId);
        directorStorage.delete(DirectorMapper.dtoFromModel(director));
    }
}
