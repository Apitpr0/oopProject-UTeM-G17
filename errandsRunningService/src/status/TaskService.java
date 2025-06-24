package status;

import java.sql.*;
import util.DBConnection;

public class TaskService {

    // Method to update the task status in the database
    public void updateTaskStatus(int taskId, String newStatus) {
        String currentStatus = getCurrentStatus(taskId);  // Fetch current status from the DB

        // Validate that the transition is allowed
        if (isValidTransition(currentStatus, newStatus)) {
            String query = "UPDATE tasks SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newStatus);  // Set the new status
                stmt.setInt(2, taskId);        // Specify which task to update
                stmt.executeUpdate();
                sendStatusUpdateToCustomer(taskId, newStatus);  // Notify the customer
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // If the transition is invalid, print an error message
            System.out.println("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    // Method to check if the status transition is valid
    private boolean isValidTransition(String currentStatus, String newStatus) {
        // Define valid transitions for each current status
        switch (currentStatus) {
            case "picked_up":
                return newStatus.equals("on_the_way");
            case "on_the_way":
                return newStatus.equals("arrived") || newStatus.equals("missing");
            case "arrived":
                return false;  // No further status transitions after "arrived"
            case "missing":
                return false;  // No further status transitions after "missing"
            default:
                return false;  // If the current status is invalid
        }
    }

    // Method to fetch the current status of a task from the database
    private String getCurrentStatus(int taskId) {
        String currentStatus = "";
        String query = "SELECT status FROM tasks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, taskId);  // Use the taskId to fetch the status
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentStatus = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentStatus;  // Return the current status of the task
    }

    // Method to send status update to the customer (this can be enhanced for real notifications)
    private void sendStatusUpdateToCustomer(int taskId, String newStatus) {
        // Logic for notifying the customer (using the Communication class or another notification service)
        System.out.println("Task " + taskId + " status updated to: " + newStatus);
        // You can add additional logic here to send an email/SMS, or update the UI
    }
}
