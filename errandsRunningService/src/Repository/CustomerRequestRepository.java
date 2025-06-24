package Repository;

import model.CustomerRequest;
import java.sql.*;
import java.util.Optional;

public class CustomerRequestRepository {
    private final Connection connection;

    public CustomerRequestRepository(Connection connection) {
        this.connection = connection;
    }

    public Optional<CustomerRequest> findById(int requestId) throws SQLException {
        String query = "SELECT * FROM cust_request WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new CustomerRequest(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("task_description"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getString("pickup_address"),
                        rs.getString("delivery_address"),
                        rs.getString("urgency"),
                        rs.getDouble("additional_charge"),
                        rs.getObject("assigned_runner_id", Integer.class)
                ));
            }
            return Optional.empty();
        }
    }
}