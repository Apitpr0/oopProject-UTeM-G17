package status;

import java.sql.*;
import util.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.util.List;  // Correct import for List
import java.util.Arrays;  // For Arrays.asList()
import java.util.*;

// Main Notification class to handle task status updates and notifications
public class notification {

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
                System.out.println("Error updating task status: " + e.getMessage());
            }
        } else {
            // If the transition is invalid, print an error message
            System.out.println("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    // Method to check if the status transition is valid
    private boolean isValidTransition(String currentStatus, String newStatus) {
        // Example of using a Map for valid transitions (for clarity)
        final Map<String, List<String>> validTransitions = new HashMap<>();
        validTransitions.put("picked_up", Arrays.asList("on_the_way"));
        validTransitions.put("on_the_way", Arrays.asList("arrived", "missing"));
        validTransitions.put("arrived", Collections.emptyList());  // No transitions allowed after "arrived"
        validTransitions.put("missing", Collections.emptyList());  // No transitions allowed after "missing"

        List<String> allowedTransitions = validTransitions.getOrDefault(currentStatus, Collections.emptyList());
        return allowedTransitions.contains(newStatus);
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
            System.out.println("Error fetching current status: " + e.getMessage());
        }
        return currentStatus;  // Return the current status of the task
    }

    // Method to send status update to the customer (this can be enhanced for real notifications)
    private void sendStatusUpdateToCustomer(int taskId, String newStatus) {
        // Trigger desktop notification for customer update
        TaskStatusNotificationApp.showNotification(newStatus);  // Trigger desktop notification
        System.out.println("Task " + taskId + " status updated to: " + newStatus);
    }

    // Nested TaskStatusNotificationApp class for sending system tray notifications
    public static class TaskStatusNotificationApp {

        private static SystemTray systemTray;
        private static TrayIcon trayIcon;

        // Initialize the tray icon once when the application starts
        public static void initializeTrayIcon() {
            try {
                // Initialize the tray icon once
                systemTray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("icon.png"), "Task Status");
                systemTray.add(trayIcon);  // Register the tray icon with the system tray
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        // Show notification based on task status
        public static void showNotification(String status) {
            if (trayIcon == null) {
                initializeTrayIcon();  // Initialize if not done already
            }

            // Display appropriate notification based on the status
            String message = getMessageForUserType(status);
            trayIcon.displayMessage("Package Status", message, TrayIcon.MessageType.INFO);
        }

        // Get message for the user based on the task status
        private static String getMessageForUserType(String status) {
            String message = "";
            switch (status) {
                case "picked_up":
                    message = "Your package has been picked up!";
                    break;
                case "on_the_way":
                    message = "Your package is on the way!";
                    break;
                case "arrived":
                    message = "Your package has arrived!";
                    break;
                case "missing":
                    message = "There was an issue with your package.";
                    break;
                default:
                    message = "Status updated.";
            }
            return message;
        }
    }
}
