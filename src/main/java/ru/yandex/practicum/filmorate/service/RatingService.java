package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.dal.model.Rating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.mapper.RatingDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.rating.RatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<RatingDto> findAll() {
        return RatingDtoMapper.dtoFromModel(ratingStorage.findAll());
    }

    public RatingDto findRatingById(Long ratingId) {
        Rating rating = ratingStorage.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating not found. Id: " + ratingId));

        return RatingDtoMapper.dtoFromModel(rating);
    }
}
