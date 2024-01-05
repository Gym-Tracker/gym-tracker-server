package com.example.gymtrackerserver;

import java.sql.Timestamp;

public record Workout(Timestamp date, int duration, Exercise[] exercises) {
}
