INSERT INTO films (rating_id, name, description, release_date, duration)
VALUES (1, 'Test film 1', 'Description 1', '2024-01-01', 60),
       (2, 'Test film 2', 'Description 2', '2024-02-01', 120);

INSERT INTO users (email, login, name, birthday)
VALUES ('admin@localhost', 'admin', 'Admin', '1970-01-01'),
       ('moderator@localhost', 'moderator', 'Moderator', '1970-02-01');

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (2, 5);
