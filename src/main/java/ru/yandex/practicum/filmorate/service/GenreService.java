package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenreService {

    private GenreStorage genreStorage;

    @Autowired
    public GenreService(@Qualifier("dBGenreStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return new ArrayList<>(genreStorage.getAllGenres().values());
    }

    public Genre getGenre(int genreId) {
        return genreStorage.getGenre(genreId);
    }
}
