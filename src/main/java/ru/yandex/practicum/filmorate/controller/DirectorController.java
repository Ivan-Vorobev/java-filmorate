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
import ru.yandex.practicum.filmorate.model.Director;
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
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findDirector(
            @PathVariable("id") Long directorId
    ) {
        return directorService.findDirector(directorId);
    }

    @PostMapping
    @Validated(RequestMethod.Create.class)
    public Director add(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    @Validated({RequestMethod.Update.class})
    public Director update(@Valid @RequestBody Director director) {
        Director updatedDirector = directorService.update(director);
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
