package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.mapper.DirectorDtoMapper;
import ru.yandex.practicum.filmorate.dal.model.Director;
import ru.yandex.practicum.filmorate.dal.storage.director.DirectorStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<DirectorDto> findAll() {
        return directorStorage.findAll().stream()
                .map(DirectorDtoMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public DirectorDto findDirectorByID(Long directorId) {
        Director director = directorStorage.findById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found. Id: " + directorId));

        return DirectorDtoMapper.modelFromDto(director);
    }

    public DirectorDto create(DirectorDto directorDto) {
        return DirectorDtoMapper.modelFromDto(
                directorStorage.add(
                        DirectorDtoMapper.dtoFromModel(directorDto)
                )
        );
    }

    public DirectorDto update(DirectorDto directorDto) {
        findDirectorByID(directorDto.getId());
        return DirectorDtoMapper.modelFromDto(
                directorStorage.update(
                        DirectorDtoMapper.dtoFromModel(directorDto)
                )
        );
    }

    public void delete(Long direcorId) {
        DirectorDto director = findDirectorByID(direcorId);
        directorStorage.delete(DirectorDtoMapper.dtoFromModel(director));
    }
}
