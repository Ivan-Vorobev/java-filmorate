package ru.yandex.practicum.filmorate.storage.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFriendsDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    private Long friendId;
}
