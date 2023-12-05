CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_id INTEGER NOT NULL REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER REFERENCES films (film_id),
    genre_id INTEGER REFERENCES genres (genre_id),
    PRIMARY KEY(film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER REFERENCES users (user_id),
    friend_id INTEGER REFERENCES users (user_id),
    PRIMARY KEY (user_id, friend_id)
);


CREATE TABLE IF NOT EXISTS likes (
    user_id INTEGER REFERENCES users (user_id),
    film_id INTEGER REFERENCES films (film_id),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users (user_id),
    film_id INTEGER NOT NULL REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS review_user_likes (
    review_id INTEGER REFERENCES reviews (review_id),
    user_id INTEGER REFERENCES users (user_id),
    is_positive BOOLEAN NOT NULL,
    PRIMARY KEY (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS event_types (
    event_type_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS operations (
    operation_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS feed (
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp timestamp NOT NULL DEFAULT(CURRENT_TIMESTAMP()),
    user_id INTEGER NOT NULL REFERENCES users (user_id),
    event_type_id INTEGER NOT NULL REFERENCES event_types (event_type_id),
    operation_id INTEGER NOT NULL REFERENCES operations (operation_id),
    entity_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id INTEGER REFERENCES films (film_id),
    director_id INTEGER REFERENCES directors (director_id),
    PRIMARY KEY (film_id, director_id)
);