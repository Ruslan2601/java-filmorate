package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.*;

@Component
public class DBLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBLikesStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Integer> getLikes(int filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> resultSet.getInt("user_id"),
                filmId));
    }

    public void addLike(int userId, int filmId) {
        String sqlQuery = "INSERT INTO likes (user_id, film_id) " +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public void deleteLike(int userId, int filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?;";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public void deleteUserLikes(int userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
    }

    public void deleteFilmLikes(int filmId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    public Set<Integer> getLikesFilm(int userId) {
        String sqlQuery = "SELECT film_id FROM likes WHERE user_id = ?;";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> resultSet.getInt("film_id"),
                userId));
    }

    public Map<Integer, List<Integer>> getUsersLikes(int userId) {
        String sqlQuery = "select user_id, film_id from likes;";
        Map<Integer, List<Integer>> result = new HashMap<>();
        jdbcTemplate.query(sqlQuery, (ResultSet rs) -> {
            int thisUserId = rs.getInt("user_id");
            int thisFilmId = rs.getInt("film_id");
            if (!result.containsKey(thisUserId)) {
                result.put(thisUserId, new ArrayList<>());
            }
            result.get(thisUserId).add(thisFilmId);
        });
        return result;
    }
}
