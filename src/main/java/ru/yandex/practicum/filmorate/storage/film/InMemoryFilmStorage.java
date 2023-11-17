package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int lastId;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        lastId = 1;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
    }

    @Override
    public Film getFilm(int filmId) {
        checkContainsFilm(filmId);
        return films.get(filmId);
    }

    @Override
    public Film addFilm(Film film) {
        checkNonContainsFilm(film.getId());
        film.setId(lastId++);
        films.put(film.getId(), fillingOptionalParameters(film));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkContainsFilm(film.getId());
        films.put(film.getId(), fillingOptionalParameters(film));
        return film;
    }

    @Override
    public Film deleteFilm(int filmId) {
        Film film = getFilm(filmId);
        films.remove(filmId);
        return film;
    }

    private Film fillingOptionalParameters(Film film) {
        if (film.getDescription() == null) {
            film.setDescription("");
        }
        if (film.getUserLikes() == null) {
            film.setUserLikes(new HashSet<>());
        }
        return film;
    }

    private void checkNonContainsFilm(int filmId) {
        if (films.containsKey(filmId)) {
            throw new AddExistObjectException("Film с указанным id = " + filmId + " уже существует");
        }
    }

    private void checkContainsFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new UpdateNonExistObjectException("Film с указанным id = " + filmId + " не существует");
        }
    }
}