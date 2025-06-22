package dao;

import model.Runner;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerDAO {

    //  Get all runners with their availability (for dashboard display)
    public static List<Runner> getAllRunnersWithAvailability() {
        List<Runner> runners = new ArrayList<>();

        String sql = """
            SELECT u.id, u.name, u.email, ra.day_of_week, ra.start_time, ra.end_time
            FROM users u
            LEFT JOIN runner_availability ra ON u.id = ra.runner_id
            WHERE u.role = 'Runner'
            ORDER BY u.id ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", // password not needed for display
                        rs.getString("day_of_week") != null ? rs.getString("day_of_week") : "-",
                        rs.getString("start_time") != null ? rs.getString("start_time") : "-",
                        rs.getString("end_time") != null ? rs.getString("end_time") : "-"
                );
                runners.add(runner);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch runners with availability: " + e.getMessage());
        }

        return runners;
    }

    public static List<Runner> getAvailableRunnersByTime() {
        List<Runner> availableRunners = new ArrayList<>();

        String sql = """
        SELECT u.id, u.name, u.email, ra.day_of_week, ra.start_time, ra.end_time
        FROM users u
        JOIN runner_availability ra ON u.id = ra.runner_id
        WHERE u.role = 'Runner'
          AND ra.day_of_week = DAYNAME(CURDATE())
          AND CURTIME() BETWEEN ra.start_time AND ra.end_time
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
            System.err.println("❌ Error fetching time-based available runners: " + e.getMessage());
        }

        return availableRunners;
    }



    // ✅ Get all generally available runners (used for UI)
    public static List<Runner> getAvailableRunners() {
        List<Runner> availableRunners = new ArrayList<>();

        String sql = "SELECT id, name, email FROM users WHERE role = 'Runner'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", "-", "-", "-"
                );

                availableRunners.add(runner);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching available runners: " + e.getMessage());
        }

        return availableRunners;
    }

    // ✅ Get only Bob (runner_id = 2) if he's available and has a schedule
    public static List<Runner> getScheduledRunners() {
        List<Runner> scheduledRunners = new ArrayList<>();

        String sql = """
            SELECT u.id, u.name, u.email, ra.day_of_week, ra.start_time, ra.end_time
            FROM users u
            JOIN runner_availability ra ON u.id = ra.runner_id
            WHERE u.role = 'Runner'
              AND u.id = 2
              AND ra.day_of_week = DAYNAME(CURDATE())
              AND CURTIME() BETWEEN ra.start_time AND ra.end_time
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", // password not needed
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                );
                scheduledRunners.add(runner);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch scheduled runners: " + e.getMessage());
        }

        return scheduledRunners;
    }


    // ✅ Always return Bob's ID (2) without checking schedule or availability
    public static Integer getBobIfAvailableNow() {
        return 2;
    }



    // ✅ Update runner availability flag (1 = available, 0 = not)
// BUT always force Bob (runner_id = 2) to remain available
    public static boolean setRunnerAvailability(int runnerId, boolean available) {
        String sql;

        if (runnerId == 2) {
            // Force Bob to always be available
            sql = "UPDATE users SET is_available = 1 WHERE id = ? AND role = 'Runner'";
        } else {
            // For other runners, allow regular updates
            sql = "UPDATE users SET is_available = ? WHERE id = ? AND role = 'Runner'";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (runnerId == 2) {
                stmt.setInt(1, runnerId); // Only 1 parameter (id) for Bob
            } else {
                stmt.setBoolean(1, available);
                stmt.setInt(2, runnerId);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to update runner availability: " + e.getMessage());
            return false;
        }
    }

}

