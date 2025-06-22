package dao;

import model.RunnerAssignment;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerAssignmentDAO {

    // Get all assignments for a specific runner
    public static List<RunnerAssignment> getAssignmentsByRunner(int runnerId) {
        List<RunnerAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM runner_assignments WHERE runner_id = ? ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RunnerAssignment assignment = new RunnerAssignment(
                        rs.getInt("id"),
                        rs.getInt("runner_id"),
                        rs.getString("errand_title"),
                        rs.getString("errand_description"),
                        rs.getString("status")
                );
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching runner assignments: " + e.getMessage());
        }
        return assignments;
    }

    // Assign runner to errand - ONLY allows Bob (runner_id = 2)
    public static boolean assignRunnerToErrand(int runnerId, int errandId) {
        if (runnerId != 2) {
            System.out.println("❌ Assignment blocked: Only Bob (runner_id=2) can be assigned");
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

    // Special method for Bob assignments
    public static boolean assignBobToErrandIfAvailable(int errandId, String errandTitle, String errandDesc) {
        // Use getBobIfAvailableNow() instead of isBobAvailable()
        Integer bobId = RunnerDAO.getBobIfAvailableNow();
        if (bobId == null) {
            System.out.println("❌ Bob is not currently available");
            return false;
        }

        String sql = """
            INSERT INTO runner_assignments 
            (runner_id, errand_id, errand_title, errand_description, status)
            VALUES (?, ?, ?, ?, 'assigned')""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bobId);  // Use the verified ID
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

    // Update status of an assignment
    public static boolean updateStatus(int assignmentId, String newStatus) {
        String sql = "UPDATE runner_assignments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, assignmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Failed to update assignment status: " + e.getMessage());
            return false;
        }
    }

    // Get all active assignments
    public static List<RunnerAssignment> getAllActiveAssignments() {
        List<RunnerAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM runner_assignments WHERE status != 'completed' ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RunnerAssignment assignment = new RunnerAssignment(
                        rs.getInt("id"),
                        rs.getInt("runner_id"),
                        rs.getString("errand_title"),
                        rs.getString("errand_description"),
                        rs.getString("status")
                );
                assignments.add(assignment);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching active assignments: " + e.getMessage());
        }
        return assignments;
    }
}