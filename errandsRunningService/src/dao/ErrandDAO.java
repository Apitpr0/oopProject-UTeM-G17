package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.RunnerAvailability;
import util.DBConnection;

public class ErrandDAO {

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

    public static List<RunnerAvailability> getAvailabilityByRunner(int runnerId) {
        List<RunnerAvailability> list = new ArrayList<>();
        String sql = "SELECT * FROM runner_availability WHERE runner_id = ?";
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

    public static boolean updateAvailability(RunnerAvailability availability) {
        String sql = "UPDATE runner_availability SET day_of_week = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, availability.getDayOfWeek());
            stmt.setTime(2, availability.getStartTime());
            stmt.setTime(3, availability.getEndTime());
            stmt.setInt(4, availability.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
}
