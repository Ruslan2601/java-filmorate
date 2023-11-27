package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DBFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;

@JdbcTest
public class DBFilmGenreStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final DBFilmGenreStorage filmGenreStorage;
    private final DBFilmStorage filmStorage;

    @Autowired
    public DBFilmGenreStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = new DBFilmGenreStorage(jdbcTemplate);
        this.filmStorage = new DBFilmStorage(jdbcTemplate);
    }

    @BeforeEach
    public void fillFilmGenres() {
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;");
        fillFilms();

        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?);";
        for (int i = 1; i <= 5; ++i) {
            for (int j = 1; j <= 3; ++j) {
                jdbcTemplate.update(sqlQuery, i, j);
            }
        }
    }

    public Film createFilm(int id, String name, String description, int year, int month, int day,
                           int duration, Mpa mpa) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(year, month, day));
        film.setDuration(duration);
        film.setMpa(mpa);
        film.setGenres(new HashSet<>());
        film.setUserLikes(new HashSet<>());
        return film;
    }

    public void fillFilms() {
        for (int i = 1; i <= 5; ++i) {
            filmStorage.addFilm(createFilm(0, "name " + i, "description " + i, 2000, 1, i,
                    100 + i, new Mpa(1, "")));
        }
    }

    @Test
    public void getsFilmGenre() {
        Assertions.assertEquals(3, filmGenreStorage.getFilmGenre(1).size(),
                "Все жанры указанного фильма возвращаются неправильно");

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        Assertions.assertTrue(filmGenreStorage.getFilmGenre(1).contains(genre),
                "Все жанры указанного фильма возвращаются неправильно");

        genre.setId(2);
        genre.setName("Драма");

        Assertions.assertTrue(filmGenreStorage.getFilmGenre(1).contains(genre),
                "Все жанры указанного фильма возвращаются неправильно");

        genre.setId(3);
        genre.setName("Мультфильм");

        Assertions.assertTrue(filmGenreStorage.getFilmGenre(1).contains(genre),
                "Все жанры указанного фильма возвращаются неправильно");
    }

    @Test
    public void addFilmGenre() {
        filmGenreStorage.addFilmGenre(5, 5);
        Genre genre = new Genre();
        genre.setId(5);
        genre.setName("Документальный");

        Assertions.assertTrue(filmGenreStorage.getFilmGenre(5).contains(genre),
                "Новый жанр для указаного фильма добавляется неправильно");
    }

    @Test
    public void deleteFilmGenre() {
        filmGenreStorage.deleteFilmGenre(1, 1);
        Genre genre = new Genre();

        Assertions.assertEquals(2, filmGenreStorage.getFilmGenre(1).size(),
                "Жанр указанного фильма удаляется неправильно");

        genre.setId(1);
        genre.setName("Комедия");

        Assertions.assertFalse(filmGenreStorage.getFilmGenre(1).contains(genre),
                "Жанр указанного фильма удаляется неправильно");

        filmGenreStorage.deleteFilmGenre(1, 2);

        Assertions.assertEquals(1, filmGenreStorage.getFilmGenre(1).size(),
                "Жанр указанного фильма удаляется неправильно");

        genre.setId(2);
        genre.setName("Драма");

        Assertions.assertFalse(filmGenreStorage.getFilmGenre(1).contains(genre),
                "Жанр указанного фильма удаляется неправильно");

        genre.setId(3);
        genre.setName("Мультфильм");

        Assertions.assertTrue(filmGenreStorage.getFilmGenre(1).contains(genre),
                "Жанр указанного фильма удаляется неправильно");
    }

    @Test
    public void deleteFilmGenres() {
        filmGenreStorage.deleteFilmGenres(1);

        Assertions.assertEquals(0, filmGenreStorage.getFilmGenre(1).size(),
                "Жанры указанного фильма удаляются неправильно");
    }
}
