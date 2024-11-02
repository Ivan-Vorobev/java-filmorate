package ru.yandex.practicum.filmorate.dal.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFriends {
    private Long userId;
    private Long friendId;
}
