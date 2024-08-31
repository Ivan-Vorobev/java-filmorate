package ru.yandex.practicum.filmorate.services;

public class GenerateIdService {
    private Long currentId = 0L;

    public Long generate() {
        return ++currentId;
    }
}
