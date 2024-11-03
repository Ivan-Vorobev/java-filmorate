package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDto findDirectorById(
            @PathVariable("id") Long directorId
    ) {
        return directorService.findDirectorByID(directorId);
    }

    @PostMapping
    @Validated(RequestMethod.Create.class)
    public DirectorDto create(@Valid @RequestBody DirectorDto directorDto) {
        return directorService.create(directorDto);
    }

    @PutMapping
    @Validated({RequestMethod.Update.class})
    public DirectorDto update(@Valid @RequestBody DirectorDto directorDto) {
        DirectorDto updatedDirector = directorService.update(directorDto);
        log.info("Director updated. Id: " + updatedDirector.getId());
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable("id") Long directorId
    ) {
        directorService.delete(directorId);
    }
}
