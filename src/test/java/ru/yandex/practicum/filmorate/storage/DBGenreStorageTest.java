package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.DBGenreStorage;

import java.util.HashMap;
import java.util.Map;

@JdbcTest
public class DBGenreStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final DBGenreStorage genreStorage;

    @Autowired
    public DBGenreStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = new DBGenreStorage(jdbcTemplate);
    }

    @Test
    public void getAllGenres() {
        Map<Integer, Genre> genres = new HashMap<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.put(1, genre);

        genre = new Genre();
        genre.setId(2);
        genre.setName("Драма");
        genres.put(2, genre);

        genre = new Genre();
        genre.setId(3);
        genre.setName("Мультфильм");
        genres.put(3, genre);

        genre = new Genre();
        genre.setId(4);
        genre.setName("Триллер");
        genres.put(4, genre);

        genre = new Genre();
        genre.setId(5);
        genre.setName("Документальный");
        genres.put(5, genre);

        genre = new Genre();
        genre.setId(6);
        genre.setName("Боевик");
        genres.put(6, genre);

        Assertions.assertEquals(genres, genreStorage.getAllGenres(),
                "Все жанры возвращаются неправильно");
    }

    @Test
    public void getExistGenre() {
        Genre genre = new Genre();

        genre.setId(1);
        genre.setName("Комедия");
        Assertions.assertEquals(genre, genreStorage.getGenre(1),
                "Жанр с указанынм id возвращается неправильно");

        genre.setId(2);
        genre.setName("Драма");
        Assertions.assertEquals(genre, genreStorage.getGenre(2),
                "Жанр с указанынм id возвращается неправильно");

        genre.setId(3);
        genre.setName("Мультфильм");
        Assertions.assertEquals(genre, genreStorage.getGenre(3),
                "Жанр с указанынм id возвращается неправильно");

        genre.setId(4);
        genre.setName("Триллер");
        Assertions.assertEquals(genre, genreStorage.getGenre(4),
                "Жанр с указанынм id возвращается неправильно");

        genre.setId(5);
        genre.setName("Документальный");
        Assertions.assertEquals(genre, genreStorage.getGenre(5),
                "Жанр с указанынм id возвращается неправильно");

        genre.setId(6);
        genre.setName("Боевик");
        Assertions.assertEquals(genre, genreStorage.getGenre(6),
                "Жанр с указанынм id возвращается неправильно");
    }

    @Test
    public void getNonExistGenre() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> genreStorage.getGenre(0),
                "При добавлении фильма ожидалось UpdateNonExistObjectException");
    }
}
