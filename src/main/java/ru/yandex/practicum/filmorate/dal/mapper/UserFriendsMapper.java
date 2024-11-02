package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.model.UserFriends;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserFriendsMapper implements RowMapper<UserFriends> {
    @Override
    public UserFriends mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserFriends.builder()
                .userId(rs.getLong("user_id"))
                .friendId(rs.getLong("friend_id"))
                .build();
    }
}
