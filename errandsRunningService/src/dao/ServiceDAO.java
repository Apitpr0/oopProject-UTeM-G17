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
    public boolean submitRating(int taskId, int customerId, int runnerId, int rating, String comment) {
        String sql = "INSERT INTO ratings (request_id, customer_id, runner_id, rating, comments) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            stmt.setInt(2, customerId);
            stmt.setInt(3, runnerId);
            stmt.setInt(4, rating);
            stmt.setString(5, comment); // ✅ new line for comment

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<ServiceRequest> getCompletedRequestsByCustomer(int customerId) {
        List<ServiceRequest> completedList = new ArrayList<>();

        String sql = """
        SELECT cr.id AS request_id,
               cr.task_description,
               cr.pickup_address,
               cr.delivery_address,
               cr.urgency,
               cr.additional_charge,
               ra.status AS runner_status,
               u.id AS runner_id,
               u.name AS runner_name,
               r.rating
        FROM cust_request cr
        JOIN runner_assignments ra ON ra.request_id = cr.id
        JOIN users u ON ra.runner_id = u.id
        LEFT JOIN ratings r ON r.request_id = cr.id  -- Make sure this is correct
        WHERE ra.status = 'Completed' AND cr.customer_id = ?
        ORDER BY cr.id DESC;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId); // ✅ NOW it's valid
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ServiceRequest req = new ServiceRequest(
                        customerId,
                        rs.getString("task_description"),
                        rs.getString("pickup_address"),
                        rs.getString("delivery_address")
                );

                req.setId(rs.getInt("request_id"));
                req.setUrgency(rs.getString("urgency"));
                req.setStatus(rs.getString("runner_status"));
                req.setAdditionalCharge(rs.getDouble("additional_charge"));
                req.setRating(rs.getInt("rating"));
                req.setRunnerId(rs.getInt("runner_id"));

                completedList.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completedList;
    }


}