package dao;

import model.RunnerAvailability;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerAvailabilityDAO {

    // Insert new availability
    public static boolean addAvailability(RunnerAvailability availability) {
        String sql = "INSERT INTO runner_availability (runner_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, availability.getRunnerId());
            stmt.setString(2, availability.getDayOfWeek());
            stmt.setTime(3, availability.getStartTime());
            stmt.setTime(4, availability.getEndTime());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve availability entries for a specific runner
    public static List<RunnerAvailability> getAvailabilityByRunner(int runnerId) {
        List<RunnerAvailability> list = new ArrayList<>();
        String sql = "SELECT * FROM runner_availability WHERE runner_id = ? ORDER BY day_of_week, start_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RunnerAvailability a = new RunnerAvailability(
                        rs.getInt("id"),
                        rs.getInt("runner_id"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                );
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Delete a specific availability entry
    public static boolean deleteAvailability(int id) {
        String sql = "DELETE FROM runner_availability WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check for overlapping availability for a runner
    public static boolean hasOverlap(RunnerAvailability newEntry) {
        String sql = "SELECT COUNT(*) FROM runner_availability " +
                "WHERE runner_id = ? AND day_of_week = ? " +
                "AND (start_time < ? AND end_time > ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newEntry.getRunnerId());
            stmt.setString(2, newEntry.getDayOfWeek());
            stmt.setTime(3, newEntry.getEndTime());     // existing.start < new.end
            stmt.setTime(4, newEntry.getStartTime());   // existing.end > new.start

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
