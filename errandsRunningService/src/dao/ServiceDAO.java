package dao;

import model.ServiceRequest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    // 🔹 Insert request without assigned runner
    public boolean insertRequest(ServiceRequest request) {
        String sql = "INSERT INTO cust_request (customer_id, task_description, pickup_address, delivery_address, urgency, additional_charge, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, request.getCustomerId());
            stmt.setString(2, request.getTaskDescription());
            stmt.setString(3, request.getPickupAddress());
            stmt.setString(4, request.getDeliveryAddress());
            stmt.setString(5, request.getUrgency());
            stmt.setDouble(6, request.getAdditionalCharge());
            stmt.setString(7, request.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to insert request: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Insert request with runner assignment
    public boolean insertRequestWithRunner(ServiceRequest request, int runnerId) {
        String sql = "INSERT INTO cust_request (customer_id, task_description, pickup_address, delivery_address, urgency, additional_charge, status, assigned_runner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, request.getCustomerId());
            stmt.setString(2, request.getTaskDescription());
            stmt.setString(3, request.getPickupAddress());
            stmt.setString(4, request.getDeliveryAddress());
            stmt.setString(5, request.getUrgency());
            stmt.setDouble(6, request.getAdditionalCharge());
            stmt.setString(7, request.getStatus());
            stmt.setInt(8, runnerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to insert request with runner: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Get all requests by customer
    public List<ServiceRequest> getRequestsByCustomer(int customerId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM cust_request WHERE customer_id = ? ORDER BY id ASC";

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
                        rs.getInt("assigned_runner_id") // ✅ 9th parameter
                );
                requests.add(request);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching requests: " + e.getMessage());
        }

        return requests;
    }
}
