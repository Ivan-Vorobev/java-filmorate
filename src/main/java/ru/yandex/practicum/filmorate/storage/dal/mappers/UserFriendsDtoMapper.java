package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserFriendsDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserFriendsDtoMapper implements RowMapper<UserFriendsDto> {
    @Override
    public UserFriendsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserFriendsDto.builder()
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .build();
    }
}
