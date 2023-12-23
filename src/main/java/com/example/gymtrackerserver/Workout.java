package com.example.gymtrackerserver;

import java.util.Date;

public record Workout(Date date, int duration, Exercise[] exercises) {
}
