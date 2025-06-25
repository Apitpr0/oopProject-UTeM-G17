package dao;

import model.ServiceRequest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public boolean insertRequest(ServiceRequest request) {
        String sql = "INSERT INTO cust_request (customer_id, task_description, pickup_address, " +
                "delivery_address, urgency, additional_charge, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getCustomerId());
            stmt.setString(2, request.getTaskDescription());
            stmt.setString(3, request.getPickupAddress());
            stmt.setString(4, request.getDeliveryAddress());
            stmt.setString(5, request.getUrgency());
            stmt.setDouble(6, request.getAdditionalCharge());
            stmt.setString(7, request.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        request.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Failed to insert request: " + e.getMessage());
            return false;
        }
    }

    public boolean insertRequestWithRunner(ServiceRequest request, int runnerId) {
        String sql = "INSERT INTO cust_request (customer_id, task_description, pickup_address, " +
                "delivery_address, urgency, additional_charge, status, assigned_runner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getCustomerId());
            stmt.setString(2, request.getTaskDescription());
            stmt.setString(3, request.getPickupAddress());
            stmt.setString(4, request.getDeliveryAddress());
            stmt.setString(5, request.getUrgency());
            stmt.setDouble(6, request.getAdditionalCharge());
            stmt.setString(7, request.getStatus());
            stmt.setInt(8, runnerId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        request.setId(rs.getInt(1));
                        request.setAssignedRunnerId(runnerId);
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Failed to insert request with runner: " + e.getMessage());
            return false;
        }
    }

    public List<ServiceRequest> getRequestsByCustomer(int customerId) {
        List<ServiceRequest> requests = new ArrayList<>();
        String sql = "SELECT cr.*, u.name as runner_name FROM cust_request cr " +
                "LEFT JOIN users u ON cr.assigned_runner_id = u.id " +
                "WHERE customer_id = ? ORDER BY cr.id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ServiceRequest request = new ServiceRequest();
                request.setId(rs.getInt("id"));
                request.setCustomerId(rs.getInt("customer_id"));
                request.setTaskDescription(rs.getString("task_description"));
                request.setStatus(rs.getString("status"));
                request.setPickupAddress(rs.getString("pickup_address"));
                request.setDeliveryAddress(rs.getString("delivery_address"));
                request.setUrgency(rs.getString("urgency"));
                request.setAdditionalCharge(rs.getDouble("additional_charge"));
                request.setAssignedRunnerId(rs.getInt("assigned_runner_id"));
                request.setRunnerName(rs.getString("runner_name"));
                requests.add(request);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching requests: " + e.getMessage());
        }

        return requests;
    }

    public ServiceRequest getRequestById(int requestId) {
        String sql = "SELECT cr.*, u.name as runner_name FROM cust_request cr " +
                "LEFT JOIN users u ON cr.assigned_runner_id = u.id " +
                "WHERE cr.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ServiceRequest request = new ServiceRequest();
                request.setId(rs.getInt("id"));
                request.setCustomerId(rs.getInt("customer_id"));
                request.setTaskDescription(rs.getString("task_description"));
                request.setStatus(rs.getString("status"));
                request.setPickupAddress(rs.getString("pickup_address"));
                request.setDeliveryAddress(rs.getString("delivery_address"));
                request.setUrgency(rs.getString("urgency"));
                request.setAdditionalCharge(rs.getDouble("additional_charge"));
                request.setAssignedRunnerId(rs.getInt("assigned_runner_id"));
                request.setRunnerName(rs.getString("runner_name"));
                return request;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching request: " + e.getMessage());
        }

        return null;
    }
}