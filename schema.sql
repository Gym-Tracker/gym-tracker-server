CREATE TABLE "user"(
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    password BYTEA,
    salt BYTEA,
    UNIQUE (email)
);

CREATE TABLE workout(
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "user"(id),
    duration INT,
    timestamp TIMESTAMP
);

CREATE TABLE exercise(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE set(
    id SERIAL PRIMARY KEY,
    workout_id INT REFERENCES workout(id),
    exercise_id VARCHAR(255),
    type INT,
    weight NUMERIC(5, 2),
    reps INT
);

CREATE TABLE session(
    id VARCHAR(255) PRIMARY KEY,
    user_id INT REFERENCES "user"(id),
    last_used DATE,
);