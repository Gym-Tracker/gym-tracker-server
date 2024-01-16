package com.example.gymtrackerserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserIdFetcher {
    int fetchUserId(Connection conn, String session_id) throws SQLException {
        String userIdSQL = "SELECT user_id FROM session WHERE id = ?";

        PreparedStatement userIdPstmt = conn.prepareStatement(userIdSQL);
        userIdPstmt.setString(1, session_id);
        ResultSet userIdRS = userIdPstmt.executeQuery();
        userIdRS.next();
        return userIdRS.getInt("user_id");
    }
}
