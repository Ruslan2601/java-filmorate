package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("dBGenreStorage")
public class DBGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres;";
        List<Genre> result = jdbcTemplate.query(sqlQuery, DBGenreStorage::createGenre);
        Map<Integer, Genre> genres = new HashMap<>();

        for (Genre genre : result) {
            genres.put(genre.getId(), genre);
        }

        return genres;
    }

    @Override
    public Genre getGenre(int genreID) {
        return checkContainsGenre(genreID);
    }

    public static Genre createGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }

    private Genre checkContainsGenre(int genreId) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?;";
        List<Genre> genre = jdbcTemplate.query(sqlQuery, DBGenreStorage::createGenre, genreId);

        if (genre.size() != 1) {
            throw new UpdateNonExistObjectException("Genre с указанным id = " + genreId + " не существует " +
                    "или имееться больше 1");
        }

        return genre.get(0);
    }
}
