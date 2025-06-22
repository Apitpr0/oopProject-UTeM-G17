package controller;

import dao.ServiceDAO;
import dao.RunnerDAO;
import model.ServiceRequest;
import util.DBConnection;
import java.sql.*;
import java.util.List;

public class ServiceController {

    private final ServiceDAO serviceDAO;

    public ServiceController() {
        this.serviceDAO = new ServiceDAO();
    }

    public boolean submitRequest(ServiceRequest request) {
        return serviceDAO.insertRequest(request);
    }

    public boolean submitRequestWithRunnerAssignment(ServiceRequest request) {
        // Only assign to Bob (runner_id = 2) if available
        Integer bobId = RunnerDAO.getBobIfAvailableNow();

        if (bobId != null && bobId == 2) {
            request.setAssignedRunnerId(bobId);
            System.out.println("✅ Assigned Bob (runner_id=2) to request");
            return serviceDAO.insertRequestWithRunner(request, bobId);
        }

        System.out.println("❌ Bob is not available - request submitted without assignment");
        return serviceDAO.insertRequest(request);
    }

    public List<ServiceRequest> getRequestsByCustomer(int customerId) {
        return serviceDAO.getRequestsByCustomer(customerId);
    }

    public String getRunnerNameByRequestId(int requestId) {
        String sql = "SELECT u.name FROM users u JOIN cust_request r ON u.id = r.assigned_runner_id WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch assigned runner: " + e.getMessage());
        }
        return null;
    }
}