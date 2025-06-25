package dao;

import model.Runner;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerDAO {

    // ✅ Get all runners available now (based on time & day)
    public static List<Runner> getAvailableRunnersNow() {
        List<Runner> availableRunners = new ArrayList<>();

        String sql = """
            SELECT u.id, u.name, u.email, u.rating,
                   ra.day_of_week, ra.start_time, ra.end_time
            FROM users u
            JOIN runner_availability ra ON u.id = ra.runner_id
            WHERE u.role = 'runner'
              AND u.is_available = 1
              AND ra.day_of_week = DAYNAME(CURDATE())
              AND CURTIME() BETWEEN ra.start_time AND ra.end_time
            ORDER BY u.id
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", // password not needed
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getInt("rating")
                );
                availableRunners.add(runner);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching available runners: " + e.getMessage());
        }

        return availableRunners;
    }

    // ✅ For displaying all runners with their schedule
    public static List<Runner> getAllRunnersWithAvailability() {
        List<Runner> runners = new ArrayList<>();

        String sql = """
            SELECT u.id, u.name, u.email, u.rating,
                   ra.day_of_week, ra.start_time, ra.end_time
            FROM users u
            LEFT JOIN runner_availability ra ON u.id = ra.runner_id
            WHERE u.role = 'runner'
            ORDER BY u.id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", // password
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getInt("rating")
                );
                runners.add(runner);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching all runners: " + e.getMessage());
        }

        return runners;
    }

    // ✅ Update runner availability status (still keeps is_available flag)
    public static boolean setRunnerAvailability(int runnerId, boolean available) {
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
