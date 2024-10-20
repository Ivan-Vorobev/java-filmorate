package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dto.RatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<Rating> findAll() {
        return ratingStorage.findAll().stream()
                .map(RatingMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public Rating findRating(Long ratingId) {
        RatingDto rating = ratingStorage.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating not found. Id: " + ratingId));

        return RatingMapper.modelFromDto(rating);
    }

    public Map<Long, Rating> findAllAsMap() {
        HashMap<Long, Rating> ratingMap = new HashMap<>();
        for (RatingDto rating : ratingStorage.findAll()) {
            ratingMap.put(rating.getId(), RatingMapper.modelFromDto(rating));
        }

        return ratingMap;
    }
}
