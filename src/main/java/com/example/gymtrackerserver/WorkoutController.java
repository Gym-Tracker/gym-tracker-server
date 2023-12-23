package com.example.gymtrackerserver;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class WorkoutController {

    // jdbc:postgresql://<database_host>:<port>/<database_name>
    private final String url = "jdbc:postgresql://gym-tracker.ctvjp0tbn6qi.eu-west-2.rds.amazonaws.com:4323/";
    private final String user = "postgres";
    private final String password = "#V%C9Cc&P7jh59zB";

    @PostMapping("/workouts")
    Workout newWorkout(@RequestBody Workout newWorkout) {
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
