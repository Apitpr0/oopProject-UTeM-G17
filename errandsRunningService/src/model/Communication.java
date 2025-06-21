package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Communication {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/errands_db";
    private static final String USER = "root";
    private static final String PASS = "your_password";

    // Method to send a message from sender to receiver
    public void sendMessage(int senderId, int receiverId, String content) {
        String query = "INSERT INTO communication (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);  // Set senderId (either customer or runner)
            stmt.setInt(2, receiverId); // Set receiverId (the other user)
            stmt.setString(3, content); // Set the message content
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get messages for a specific user (customer or runner)
    public List<String> getMessages(int userId) {
        List<String> messages = new ArrayList<>();
        String query = "SELECT content FROM communication WHERE receiver_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);  // Set receiverId to fetch their messages
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(rs.getString("content"));  // Add message content to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
