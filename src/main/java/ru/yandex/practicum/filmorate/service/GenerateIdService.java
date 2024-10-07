package ru.yandex.practicum.filmorate.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GenerateIdService {
    private Long currentId = 0L;

    public Long generate() {
        return ++currentId;
    }
}
