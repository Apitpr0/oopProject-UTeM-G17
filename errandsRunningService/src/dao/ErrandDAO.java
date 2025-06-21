package dao;

import model.Errand;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ErrandDAO {

    public static boolean insertErrand(Errand errand, int customerId) {
        String sql = "INSERT INTO errand (customer_id, type, description, pickup_address, dropoff_address, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, errand.getType());
            stmt.setString(3, errand.getDescription());
            stmt.setString(4, errand.getPickupAddress());
            stmt.setString(5, errand.getDropoffAddress());
            stmt.setString(6, errand.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to insert errand: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateRunnerAssignment(int errandId, int runnerId) {
        String sql = "UPDATE errand SET assigned_runner_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            stmt.setInt(2, errandId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to assign runner: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateErrandStatus(int errandId, String newStatus) {
        String sql = "UPDATE errand SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, errandId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to update status: " + e.getMessage());
            return false;
        }
    }

    public static List<Errand> getErrandsByCustomer(int customerId) {
        List<Errand> errands = new ArrayList<>();
        String sql = "SELECT * FROM errand WHERE customer_id = ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Errand errand = new Errand(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("pickup_address"),
                        rs.getString("dropoff_address"),
                        rs.getString("status"),
                        rs.getInt("assigned_runner_id")
                );
                errands.add(errand);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching errands: " + e.getMessage());
        }

        return errands;
    }

    public static Errand getErrandById(int id) {
        String sql = "SELECT * FROM errand WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Errand(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("pickup_address"),
                        rs.getString("dropoff_address"),
                        rs.getString("status"),
                        rs.getInt("assigned_runner_id")
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve errand by ID: " + e.getMessage());
        }
        return null;
    }
}
