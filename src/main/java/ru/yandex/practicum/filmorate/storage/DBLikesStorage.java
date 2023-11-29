package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
}
