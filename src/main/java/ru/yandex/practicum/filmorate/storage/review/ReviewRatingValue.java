package ru.yandex.practicum.filmorate.storage.review;

public enum ReviewRatingValue {
    DEFAULT(0),
    INCREASE(1),
    DECREASE(-1);

    public final int value;

    ReviewRatingValue(int value) {
        this.value = value;
    }
}
