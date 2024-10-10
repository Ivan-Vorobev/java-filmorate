package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GenerateIdStorage {
    private Long currentId = 0L;

    public Long generate() {
        return ++currentId;
    }
}
