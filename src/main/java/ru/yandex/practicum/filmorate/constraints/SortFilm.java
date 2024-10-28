package ru.yandex.practicum.filmorate.constraints;

public enum SortFilm {
    LIKES,
    YEAR;

    public static SortFilm fromString(String value) {
        return switch (value.toLowerCase()) {
            case "likes" -> LIKES;
            case "year" -> YEAR;
            default -> null;
        };
    }
}
