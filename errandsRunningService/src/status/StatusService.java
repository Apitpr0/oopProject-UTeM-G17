package status;

import model.ServiceRequest;
import util.DBConnection;
import java.sql.*;
import java.util.logging.*;

public class StatusService {

    private Connection connection;
    private static final Logger logger = Logger.getLogger(StatusService.class.getName());

    public StatusService() {
        try {
            this.connection = DBConnection.getConnection(); // Use your existing DB connection utility
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed", e);
        }
    }

    // Method to get service request details by orderId
    public ServiceRequest getServiceRequestById(int orderId) {
        String query = "SELECT cr.*, u.name as runner_name FROM cust_request cr " +
                "LEFT JOIN users u ON cr.assigned_runner_id = u.id " +
                "WHERE cr.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
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
            logger.log(Level.SEVERE, "Error while fetching service request", e);
        }
        return null;
    }

    // Method to get runner's name by orderId
    public String getRunnerNameByOrderId(int orderId) {
        String query = "SELECT u.name FROM tasks t JOIN users u ON t.runner_id = u.id WHERE t.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching runner name", e);
        }
        return "Unknown";
    }

    // Method to get order status by orderId
    public String getOrderStatusByOrderId(int orderId) {
        String query = "SELECT status FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching order status", e);
        }
        return "Unknown";
    }

    // Close connection (can be used for cleanup)
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while closing connection", e);
        }
    }
}