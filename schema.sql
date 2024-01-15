CREATE TABLE "user"(
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    password TEXT,
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
    exercise_id INT REFERENCES exercise(id),
    type INT,
    weight NUMERIC(5, 2),
    reps INT
);
