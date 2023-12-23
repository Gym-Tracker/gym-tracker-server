package com.example.gymtrackerserver;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkoutController {

    @PostMapping("/workouts")
    Workout newWorkout(@RequestBody Workout newWorkout) {
        return newWorkout;
    }
}
