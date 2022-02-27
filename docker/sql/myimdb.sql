-- Database
CREATE DATABASE myimdb;
\c myimdb;

-- Directors
CREATE TABLE directors (
  id serial NOT NULL,
  PRIMARY KEY (id),
  name character varying NOT NULL,
  last_name character varying NOT NULL
);

-- Movies
CREATE TABLE movies (
  id uuid NOT NULL,
  title character varying NOT NULL,
  year_of_production smallint NOT NULL,
  director_id integer NOT NULL
);

ALTER TABLE movies
ADD CONSTRAINT movies_id PRIMARY KEY (id);
ALTER TABLE movies
ADD FOREIGN KEY (director_id) REFERENCES directors (id);

-- Actors
CREATE TABLE actors (
  id serial NOT NULL,
  PRIMARY KEY (id),
  name character varying NOT NULL
);

-- Link between movies and actors
CREATE TABLE movies_actors (
  movie_id uuid NOT NULL,
  actor_id integer NOT NULL
);

ALTER TABLE movies_actors
ADD CONSTRAINT movies_actors_id_movies_id_actors PRIMARY KEY (movie_id, actor_id);
ALTER TABLE movies_actors
ADD FOREIGN KEY (movie_id) REFERENCES movies (id);
ALTER TABLE movies_actors
ADD FOREIGN KEY (actor_id) REFERENCES actors (id);

-- Actors
INSERT INTO actors (name) VALUES ('Henry Cavill');
INSERT INTO actors (name) VALUES ('Gal Godot');
INSERT INTO actors (name) VALUES ('Ezra Miller');
INSERT INTO actors (name) VALUES ('Ben Affleck');
INSERT INTO actors (name) VALUES ('Ray Fisher');
INSERT INTO actors (name) VALUES ('Jason Momoa');
COMMIT;

-- Directors
INSERT INTO directors (name, last_name)
VALUES ('Zack', 'Snyder');
COMMIT;

-- Movies
INSERT INTO movies (id, title, year_of_production, director_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 'Zack Snyder''s Justice League', '2021', 1);
COMMIT;

-- Actor-Movie link
INSERT INTO movies_actors (movie_id, actor_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 1);
INSERT INTO movies_actors (movie_id, actor_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 2);
INSERT INTO movies_actors (movie_id, actor_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 3);
INSERT INTO movies_actors (movie_id, actor_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 4);
INSERT INTO movies_actors (movie_id, actor_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 5);
INSERT INTO movies_actors (movie_id, actor_id)
VALUES ('5e5a39bb-a497-4432-93e8-7322f16ac0b2', 6);
COMMIT;