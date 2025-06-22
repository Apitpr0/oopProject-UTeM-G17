package dao;

import model.Runner;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerDAO {

    // Check if Bob (runner_id = 2) is available
    public static boolean isBobAvailable() {
        String sql = """
            SELECT 1 FROM runner_availability 
            WHERE runner_id = 2 
            AND day_of_week = DAYNAME(CURDATE())
            AND CURTIME() BETWEEN start_time AND end_time
            LIMIT 1""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error checking Bob's availability: " + e.getMessage());
            return false;
        }
    }

    // Get Bob's ID if available (always returns 2 if available)
    public static Integer getBobIfAvailableNow() {
        return isBobAvailable() ? 2 : null;
    }

    // Get all runners (but only Bob will ever be assigned)
    public static List<Runner> getAvailableRunners() {
        List<Runner> availableRunners = new ArrayList<>();

        String sql = """
            SELECT u.id, u.name, u.email,
                   ra.day_of_week, ra.start_time, ra.end_time
            FROM users u
            JOIN runner_availability ra ON u.id = ra.runner_id
            WHERE u.role = 'runner' AND u.is_available = 1
            ORDER BY u.id""";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "",
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                );
                availableRunners.add(runner);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available runners: " + e.getMessage());
        }
        return availableRunners;
    }

    // Force Bob to always be available (override any availability settings)
    public static boolean setRunnerAvailability(int runnerId, boolean available) {
        if (runnerId == 2) {
            return true; // Bob is always considered available
        }

        String sql = "UPDATE users SET is_available = ? WHERE id = ? AND role = 'runner'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, runnerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Failed to update runner availability: " + e.getMessage());
            return false;
        }
    }
}