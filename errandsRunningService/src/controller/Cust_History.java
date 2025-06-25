package controller;

import model.ServiceRequest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cust_History {

    public List<ServiceRequest> getCompletedRequestsByCustomer(int customerId) {
        List<ServiceRequest> list = new ArrayList<>();

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
            LEFT JOIN ratings r ON r.request_id = cr.id
            WHERE ra.status = 'Completed' AND cr.customer_id = ?
            ORDER BY cr.id DESC;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
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
                req.setRunnerName(rs.getString("runner_name"));

                list.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean submitRating(int requestId, int customerId, int runnerId, int rating, String comment) {
        String sql = """
            INSERT INTO ratings (request_id, customer_id, runner_id, rating, comments)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.setInt(2, customerId);
            stmt.setInt(3, runnerId);
            stmt.setInt(4, rating);
            stmt.setString(5, comment);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Rating submission failed: " + e.getMessage());
            return false;
        }
    }
}
