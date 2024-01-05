package com.example.gymtrackerserver;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

// TODO: remove cross origin annotation
@CrossOrigin(origins = "http://127.0.0.1:5173")
@RestController
public class WorkoutController {

    // jdbc:postgresql://<database_host>:<port>/<database_name>
    private final String url = "jdbc:postgresql://gym-tracker.ctvjp0tbn6qi.eu-west-2.rds.amazonaws.com:4323/";
    private final String user = "postgres";
    private final String password = "#V%C9Cc&P7jh59zB";

    @PostMapping("/workout")
    Workout newWorkout(@RequestBody Workout newWorkout) {
        String workoutSQL = "INSERT INTO workout(user_id, duration, timestamp) VALUES(?, ?, ?)";
        String setSQL = "INSERT INTO set(workout_id, exercise_id, type, weight, reps) VALUES(?, ?, ?, ?, ?)";
        int workoutID;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);

            PreparedStatement pstmt = conn.prepareStatement(workoutSQL, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, 15);
            pstmt.setInt(2, newWorkout.duration());
            pstmt.setTimestamp(3, newWorkout.date());

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            workoutID = (int) rs.getLong(1);

            for (Exercise exercise : newWorkout.exercises()) {
                for (Set set : exercise.sets()) {
                    System.out.println(set);
                    pstmt = conn.prepareStatement(setSQL);
                    pstmt.setInt(1, workoutID);
                    pstmt.setInt(2, 1);
                    pstmt.setInt(3, set.type());
                    pstmt.setFloat(4, set.weight());
                    pstmt.setInt(5, set.reps());
                    pstmt.execute();
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return newWorkout;
    }

    @PostMapping("user")
    Connection newUser(@RequestBody User newUser) {
        String SQL = "INSERT INTO \"user\" (email, name, age, password) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, newUser.email());
            pstmt.setString(2, newUser.name());
            pstmt.setInt(3, newUser.age());
            pstmt.setString(4, newUser.password());

            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
