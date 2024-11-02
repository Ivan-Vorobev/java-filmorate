package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dal.model.Rating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.mapper.RatingDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.rating.RatingStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<RatingDto> findAll() {
        return ratingStorage.findAll().stream()
                .map(RatingDtoMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public RatingDto findRating(Long ratingId) {
        Rating rating = ratingStorage.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating not found. Id: " + ratingId));

        return RatingDtoMapper.modelFromDto(rating);
    }
}
