package ru.yandex.practicum.filmorate.dal.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    private Long id;
    private String name;
}
