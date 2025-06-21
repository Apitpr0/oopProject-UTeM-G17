package dao;

import model.RunnerAssignment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunnerAssignmentDAO {

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
}
