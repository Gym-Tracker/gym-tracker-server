package com.example.gymtrackerserver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

// TODO: remove cross origin annotation
@CrossOrigin(origins = "http://127.0.0.1:5173", allowCredentials = "true")
@RestController
public class WorkoutController {

    // jdbc:postgresql://<database_host>:<port>/<database_name>
    private final String url = "jdbc:postgresql://gym-tracker.ctvjp0tbn6qi.eu-west-2.rds.amazonaws.com:4323/";
    private final String user = "postgres";
    private final String password = "#V%C9Cc&P7jh59zB";

    @PostMapping("/workout")
    Workout newWorkout(@RequestBody Workout newWorkout, @CookieValue("session-id") String session_id) {
        String workoutSQL = "INSERT INTO workout(user_id, duration, timestamp) VALUES(?, ?, ?)";
        String setSQL = "INSERT INTO set(workout_id, exercise_id, type, weight, reps) VALUES(?, ?, ?, ?, ?)";
        int workoutID;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);

            int userID = new UserIdFetcher().fetchUserId(conn, session_id);

            PreparedStatement pstmt = conn.prepareStatement(workoutSQL, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userID);
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
                    pstmt.setInt(2, exercise.id());
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

    @GetMapping("/workout")
    Workout[] workouts(@CookieValue("session-id") String session_id) {
        List<Workout> workouts = new ArrayList<>();

        String workoutSQL = "SELECT * FROM workout WHERE user_id = ?";
        String setSQL = "SELECT workout_id, exercise_id, type, weight, reps"
                + " FROM set INNER JOIN workout ON workout.id = set.workout_id"
                + " WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);

            int userID = new UserIdFetcher().fetchUserId(conn, session_id);

            PreparedStatement workoutPstmt = conn.prepareStatement(workoutSQL);
            workoutPstmt.setInt(1, userID);
            ResultSet workoutRS = workoutPstmt.executeQuery();

            PreparedStatement setPstmt = conn.prepareStatement(setSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            setPstmt.setInt(1, userID);
            ResultSet setRS = setPstmt.executeQuery();

            while (workoutRS.next()) {
                int workoutID = workoutRS.getInt("id");

                // Linked hash map used as it keeps the elements in the order they were inserted
                Map<Integer, List<Set>> exercisesAndSets = new LinkedHashMap<>();
                while (setRS.next()) {
                    if (setRS.getInt("workout_id") == workoutID) {

                        int exerciseID = setRS.getInt("exercise_id");

                        int type = setRS.getInt("type");
                        float weight = setRS.getFloat("weight");
                        int reps = setRS.getInt("reps");

                        if (!exercisesAndSets.containsKey(exerciseID)) {
                            exercisesAndSets.put(exerciseID, new ArrayList<>());
                        }
                        exercisesAndSets.get(exerciseID).add(new Set(type, weight, reps));
                    }
                }

                // Reset result set of exercise sets back to beginning for next loop
                setRS.beforeFirst();

                List<Exercise> exercisesList = new ArrayList<>();
                for (Integer exerciseID : exercisesAndSets.keySet()) {

                    List<Set> setsList = exercisesAndSets.get(exerciseID);
                    Set[] sets = setsList.toArray(new Set[0]);

                    exercisesList.add(new Exercise(exerciseID, sets));
                }
                Exercise[] exercises = exercisesList.toArray(new Exercise[0]);

                workouts.add(
                        new Workout(
                                workoutRS.getTimestamp("timestamp"),
                                workoutRS.getInt("duration"),
                                exercises
                        )
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return workouts.toArray(new Workout[0]);
    }

    @PostMapping("/register")
    boolean register(@RequestBody User newUser) {
        String SQL = "INSERT INTO \"user\" (email, password) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);

            PreparedStatement pstmt = conn.prepareStatement(SQL);

            pstmt.setString(1, newUser.email());
            pstmt.setString(2, newUser.password());

            pstmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @PostMapping("/login")
    boolean login(@RequestBody User potentialUser, HttpServletResponse response) {

        String userSQL = "SELECT * FROM \"user\" WHERE email = ?";
        String sessionSQL = "INSERT INTO session (id, user_id, last_used) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);

            PreparedStatement userPstmt = conn.prepareStatement(userSQL);
            userPstmt.setString(1, potentialUser.email());

            ResultSet rs = userPstmt.executeQuery();

            if (rs.next()) {
                if (Objects.equals(potentialUser.password(), rs.getString("password"))) {
                    int userID = rs.getInt("id");
                    String sessionID = new RandomStringGenerator().generateString(128);
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                    PreparedStatement sessionPstmt = conn.prepareStatement(sessionSQL);
                    sessionPstmt.setString(1, sessionID);
                    sessionPstmt.setInt(2, userID);
                    sessionPstmt.setDate(3, sqlDate);

                    sessionPstmt.execute();

                    Cookie cookie = new Cookie("session-id", sessionID);
                    response.addCookie(cookie);

                    return true;

                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @PostMapping("/session")
    boolean session(@CookieValue("session-id") String session_id) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            int user_id = new UserIdFetcher().fetchUserId(conn, session_id);

            // if no user_id was found for this session ID then -1 is returned
            if (user_id == -1) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
