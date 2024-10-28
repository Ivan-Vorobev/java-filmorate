package ru.yandex.practicum.filmorate.storage.film;

import java.util.Optional;

public enum SearchByEnum {
    DIRECTOR("director"),
    TITLE("title");

    public final String value;

    SearchByEnum(String value) {
        this.value = value;
    }

    public static Optional<SearchByEnum> fromString(String text) {
        return Optional.ofNullable(
                switch (text.toLowerCase()) {
                    case "director" -> DIRECTOR;
                    case "title" -> TITLE;
                    default -> null;
                }
        );
    }
}
