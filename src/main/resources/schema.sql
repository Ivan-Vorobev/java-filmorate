DROP TABLE IF EXISTS films;
CREATE TABLE IF NOT EXISTS films
(
    id
                 BIGSERIAL
        PRIMARY
            KEY,
    rating_id
                 BIGINT,
    name
                 VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE         NOT NULL,
    duration     INTEGER
);

DROP TABLE IF EXISTS genre;
CREATE TABLE IF NOT EXISTS genre
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    name
        VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS rating;
CREATE TABLE IF NOT EXISTS rating
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    name
        VARCHAR(20) NOT NULL
);

DROP TABLE IF EXISTS film_genres;
CREATE TABLE IF NOT EXISTS film_genres
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    film_id
        BIGINT,
    genre_id
        BIGINT,
    UNIQUE
        (
         film_id,
         genre_id
            )
);

DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users
(
    id
             BIGSERIAL
        PRIMARY
            KEY,
    email
             VARCHAR(255),
    login    VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    birthday DATE
);

DROP TABLE IF EXISTS user_friends;
CREATE TABLE IF NOT EXISTS user_friends
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    user_id
        BIGINT,
    friend_id
        BIGINT,
    UNIQUE
        (
         user_id,
         friend_id
            )
);

DROP TABLE IF EXISTS film_likes;
CREATE TABLE IF NOT EXISTS film_likes
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    film_id
        BIGINT,
    user_id
        BIGINT,
    UNIQUE
        (
         film_id,
         user_id
            )
);