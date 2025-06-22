package controller;

import dao.ServiceDAO;
import dao.RunnerDAO;
import model.Runner;
import model.ServiceRequest;
import util.DBConnection;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ServiceController {

    private ServiceDAO serviceDAO;
    private RunnerDAO runnerDAO;

    public ServiceController() {
        serviceDAO = new ServiceDAO();
        runnerDAO = new RunnerDAO();
    }

    // 🔁 Standard request submission
    public boolean submitRequest(ServiceRequest request) {
        return serviceDAO.insertRequest(request);
    }

    // ✅ Request submission with random runner assignment
    public boolean submitRequestWithRunnerAssignment(ServiceRequest request) {
        List<Runner> availableRunners;

        if ("High".equalsIgnoreCase(request.getUrgency())) {
            // Prioritize runners with time-based availability
            availableRunners = runnerDAO.getAvailableRunnersByTime();
        } else {
            // Use any available runner
            availableRunners = runnerDAO.getAvailableRunners();
        }

        if (availableRunners.isEmpty()) {
            System.out.println("❌ No runners available for urgency: " + request.getUrgency());
            return serviceDAO.insertRequest(request); // Fallback: Submit without assignment
        }

        // ✅ Pick random runner from the filtered list
        Runner assignedRunner = availableRunners.get(new Random().nextInt(availableRunners.size()));
        request.setAssignedRunnerId(assignedRunner.getId());

        System.out.println("✅ Assigned " + assignedRunner.getName() + " for urgency: " + request.getUrgency());
        return serviceDAO.insertRequestWithRunner(request, assignedRunner.getId());
    }



    // 📦 Get customer-specific requests
    public List<ServiceRequest> getRequestsByCustomer(int customerId) {
        return serviceDAO.getRequestsByCustomer(customerId);
    }

    // 🧾 Get assigned runner's name by request ID
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
