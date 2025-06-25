package status;

import model.ServiceRequest;
import util.DBConnection;
import java.sql.*;

public class TaskService {

    private Connection connection;

    public TaskService() {
        try {
            this.connection = DBConnection.getConnection(); // Using your existing DB connection
        } catch (SQLException e) {
            System.err.println("❌ Failed to establish database connection: " + e.getMessage());
        }
    }

    // Fetch service request by ID
    public ServiceRequest getServiceRequestById(int orderId) {
        String query = "SELECT cr.*, u.name as runner_name FROM cust_request cr " +
                "LEFT JOIN users u ON cr.assigned_runner_id = u.id " +
                "WHERE cr.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ServiceRequest request = new ServiceRequest();
                request.setId(orderId);
                request.setCustomerId(rs.getInt("customer_id"));
                request.setTaskDescription(rs.getString("task_description"));
                request.setPickupAddress(rs.getString("pickup_address"));
                request.setDeliveryAddress(rs.getString("delivery_address"));
                request.setUrgency(rs.getString("urgency"));
                request.setStatus(rs.getString("status"));
                request.setAdditionalCharge(rs.getDouble("additional_charge"));
                request.setAssignedRunnerId(rs.getInt("assigned_runner_id"));
                request.setRunnerName(rs.getString("runner_name"));
                return request;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching service request: " + e.getMessage());
        }
        return null;
    }
}