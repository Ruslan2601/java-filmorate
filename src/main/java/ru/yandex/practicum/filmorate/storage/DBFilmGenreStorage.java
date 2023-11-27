package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.DBGenreStorage;

import java.util.HashSet;
import java.util.Set;

@Component("dBFilmGenreStorage")
public class DBFilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBFilmGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Genre> getFilmGenre(int filmId) {
        String sqlQuery = "SELECT g.genre_id, g.name FROM film_genres AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, DBGenreStorage::createGenre, filmId));
    }

    public void addFilmGenre(int filmId, int genreId) {
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public void deleteFilmGenre(int filmId, int genreId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public void deleteFilmGenres(int filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
