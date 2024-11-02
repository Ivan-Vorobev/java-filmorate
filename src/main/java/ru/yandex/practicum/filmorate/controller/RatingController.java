package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<RatingDto> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public RatingDto findRating(
            @PathVariable("id") Long ratingId
    ) {
        return ratingService.findRating(ratingId);
    }
}
