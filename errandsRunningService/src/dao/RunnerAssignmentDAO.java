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

    // Assign runner to errand
    public static boolean assignRunnerToErrand(int runnerId, int errandId) {

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

    // Insert runner assignment from ServiceController
    public static boolean insertRunnerAssignment(int runnerId, String errandTitle, String errandDescription, String status) {
        String sql = "INSERT INTO runner_assignments (runner_id, errand_title, errand_description, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            stmt.setString(2, errandTitle);
            stmt.setString(3, errandDescription);
            stmt.setString(4, status);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Inserted runner assignment for runner_id=" + runnerId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to insert runner assignment: " + e.getMessage());
        }

        return false;
    }

    public static String getStatusByRunnerAndTask(int runnerId, String task) {
        String status = "-";
        String sql = "SELECT status FROM runner_assignments WHERE runner_id = ? AND errand_title = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, runnerId);
            stmt.setString(2, task);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }



}
