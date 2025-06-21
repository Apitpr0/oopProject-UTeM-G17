package dao;

import model.ServiceRequest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public static boolean submitRequest(ServiceRequest request) {
        String sql = "INSERT INTO request (customer_id, task_description, status, pickup_address, delivery_address, urgency, additional_charge, assigned_runner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, request.getCustomerId());
            stmt.setString(2, request.getTaskDescription());
            stmt.setString(3, request.getStatus());
            stmt.setString(4, request.getPickupAddress());
            stmt.setString(5, request.getDeliveryAddress());
            stmt.setString(6, request.getUrgency());
            stmt.setDouble(7, request.getAdditionalCharge());
            stmt.setInt(8, request.getAssignedRunnerId()); // ✅ Include runner assignment
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Failed to submit request: " + e.getMessage());
        }
        return false;
    }

    public static List<ServiceRequest> getRequestsByCustomerId(int customerId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM request WHERE customer_id = ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ServiceRequest request = new ServiceRequest(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("task_description"),
                        rs.getString("status"),
                        rs.getString("pickup_address"),
                        rs.getString("delivery_address"),
                        rs.getString("urgency"),
                        rs.getDouble("additional_charge"),
                        rs.getInt("assigned_runner_id") // ✅ Read assigned runner
                );
                requests.add(request);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching requests: " + e.getMessage());
        }

        return requests;
    }

    public static boolean updateStatus(int requestId, String newStatus) {
        String sql = "UPDATE request SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating request status: " + e.getMessage());
        }

        return false;
    }
}
