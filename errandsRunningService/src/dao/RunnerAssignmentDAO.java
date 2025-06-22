package dao;

import model.RunnerAssignment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerAssignmentDAO {

    // ✅ Get all assignments for a specific runner
    public static List<RunnerAssignment> getAssignmentsByRunner(int runnerId) {
        List<RunnerAssignment> list = new ArrayList<>();
        String sql = "SELECT * FROM runner_assignments WHERE runner_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RunnerAssignment a = new RunnerAssignment(
                        rs.getInt("id"),
                        rs.getInt("runner_id"),
                        rs.getString("errand_title"),
                        rs.getString("errand_description"),
                        rs.getString("status")
                );
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Assign runner to errand (manual or fallback use) - ONLY allows Bob
    public static boolean assignRunnerToErrand(int runnerId, int errandId) {
        if (runnerId != 2) {
            System.out.println("❌ Auto-assignment blocked: Only Bob (runner_id=2) can be assigned.");
            return false;
        }

        String sql = "INSERT INTO runner_assignments (runner_id, errand_id, status) VALUES (?, ?, 'assigned')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            stmt.setInt(2, errandId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to assign runner: " + e.getMessage());
            return false;
        }
    }

    // ✅ Auto-assign Bob ONLY — always available logic
    public static boolean assignBobToErrandIfAvailable(int errandId, String errandTitle, String errandDesc) {
        // Always treat Bob (id=2) as available
        int bobId = 2;

        String sql = """
            INSERT INTO runner_assignments (runner_id, errand_id, errand_title, errand_description, status)
            VALUES (?, ?, ?, ?, 'assigned')
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bobId);
            stmt.setInt(2, errandId);
            stmt.setString(3, errandTitle);
            stmt.setString(4, errandDesc);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Assigned Bob (runner_id=2) to errand_id: " + errandId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to auto-assign Bob: " + e.getMessage());
        }

        return false;
    }

    // ✅ Update status of an assignment
    public static boolean updateStatus(int assignmentId, String newStatus) {
        String sql = "UPDATE runner_assignments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, assignmentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
