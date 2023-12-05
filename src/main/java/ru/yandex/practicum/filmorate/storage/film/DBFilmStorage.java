package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("dBFilmStorage")
public class DBFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM films;";
        List<Film> result = jdbcTemplate.query(sqlQuery, DBFilmStorage::createFilm);
        Map<Integer, Film> films = new HashMap<>();

        for (Film film : result) {
            films.put(film.getId(), film);
        }

        return films;
    }

    @Override
    public Film getFilm(int filmId) {
        return checkContainsFilm(filmId);
    }

    @Override
    public Film addFilm(Film film) {
        checkNonContainsFilm(film.getId());

        fillingOptionalParameters(film);
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf((film.getReleaseDate())));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId((int) Objects.requireNonNull(keyHolder.getKey()));

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkContainsFilm(film.getId());

        fillingOptionalParameters(film);
        String sqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        return film;
    }

    @Override
    public Film deleteFilm(int filmId) {
        Film film = checkContainsFilm(filmId);
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        String sqlLikes = "DELETE FROM LIKES WHERE film_id = ?;";
        String sqlReviews = "DELETE FROM reviews WHERE film_id = ?;";
        String sqlFilmDirectors = "DELETE FROM FILM_DIRECTORS WHERE film_id = ?;";
        String sqlFilmGenres = "DELETE FROM film_genres WHERE film_id = ?;";

        jdbcTemplate.update(sqlLikes, filmId);
        jdbcTemplate.update(sqlReviews, filmId);
        jdbcTemplate.update(sqlFilmDirectors, filmId);
        jdbcTemplate.update(sqlFilmGenres, filmId);
        jdbcTemplate.update(sqlQuery, filmId);

        return film;
    }

    @Override
    public List<Film> getMostLikedFilmsByGenreAndYear(int count, int genreID, int year) {
        String sqlYear = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id FROM films f LEFT JOIN likes l on f.film_id = l.film_id " +
                "WHERE Extract(year from cast(f.release_date as date)) = ?" +
                "GROUP BY f.film_id " +
                "ORDER BY count(l.user_id) desc " +
                "limit ?;";
        String sqlGenre = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id FROM films f LEFT JOIN likes l on f.film_id = l.film_id " +
                "join film_genres fg on f.film_id = fg.film_id " +
                "WHERE fg.genre_id = ?" +
                "GROUP BY f.film_id " +
                "ORDER BY count(l.user_id) desc " +
                "limit ?;";
        String sqlYearAndGenre = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id FROM films f LEFT JOIN likes l on f.film_id = l.film_id " +
                "join film_genres fg on f.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? and Extract(year from cast(f.release_date as date)) = ?" +
                "GROUP BY f.film_id " +
                "ORDER BY count(l.user_id) desc " +
                "limit ?;";

        if (genreID != 0 && year != 0) {
            return jdbcTemplate.query(sqlYearAndGenre, DBFilmStorage::createFilm, genreID, year, count);
        }
        if (genreID == 0) {
            return jdbcTemplate.query(sqlYear, DBFilmStorage::createFilm, year, count);
        }
        if (year == 0) {
            return jdbcTemplate.query(sqlGenre, DBFilmStorage::createFilm, genreID, count);
        }
        return Collections.emptyList();
    }

    public static Film createFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), ""));
        return film;
    }

    private void fillingOptionalParameters(Film film) {
        if (film.getDescription() == null) {
            film.setDescription("");
        }
        if (film.getUserLikes() == null) {
            film.setUserLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        if (film.getDirectors() == null) {
            film.setDirectors(new HashSet<>());
        }
    }

    private void checkNonContainsFilm(int filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?;";
        List<Film> film = jdbcTemplate.query(sqlQuery, DBFilmStorage::createFilm, filmId);

        if (film.size() > 0) {
            throw new AddExistObjectException("Film с указанным id = " + filmId + " уже существует");
        }
    }

    private Film checkContainsFilm(int filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?;";
        List<Film> film = jdbcTemplate.query(sqlQuery, DBFilmStorage::createFilm, filmId);

        if (film.size() != 1) {
            throw new UpdateNonExistObjectException("Film с указанным id = " + filmId + " не существует " +
                    "или имеется больше 1");
        }

        return film.get(0);
    }
}
