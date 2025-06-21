package dao;

import model.Runner;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerDAO {

    // ✅ Static method to get available runners (phone column removed)
    public static List<Runner> getAvailableRunners() {
        List<Runner> availableRunners = new ArrayList<>();

        String sql = "SELECT id, name, email, availability FROM users WHERE role = 'Runner' AND is_available = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "", // password not retrieved here
                        rs.getString("availability")
                );
                availableRunners.add(runner);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching available runners: " + e.getMessage());
        }

        return availableRunners;
    }

    // ✅ Static method to update availability
    public static boolean setRunnerAvailability(int runnerId, boolean available) {
        String sql = "UPDATE users SET is_available = ? WHERE id = ? AND role = 'Runner'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, runnerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to update runner availability: " + e.getMessage());
            return false;
        }
    }
}
