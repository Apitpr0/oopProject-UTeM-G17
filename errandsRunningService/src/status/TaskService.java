package status;

import java.sql.*;

public class TaskService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/errands_db";
    private static final String USER = "root";
    private static final String PASS = "";

    // Method to update the task status in the database
    public void updateTaskStatus(int taskId, String newStatus) {
        String query = "UPDATE tasks SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);  // Set new status (e.g., 'on the way')
            stmt.setInt(2, taskId);  // Use taskId to identify the task
            stmt.executeUpdate();

            // After updating the status, notify the customer
            sendStatusUpdateToCustomer(taskId, newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to send status update to the customer
    private void sendStatusUpdateToCustomer(int taskId, String newStatus) {
        // Logic for notifying the customer (using the Communication class)
        System.out.println("Task " + taskId + " status updated to: " + newStatus);
        // Here, you'd call a method to send a message, e.g., via email or notification
    }
}
