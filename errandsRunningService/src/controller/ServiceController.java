package controller;

import dao.ServiceDAO;
import dao.RunnerDAO;
import dao.RunnerAssignmentDAO;
import model.ServiceRequest;
import model.Runner;
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
        // ✅ New: Get runners who are available now (based on runner_availability)
        List<Runner> availableRunners = RunnerDAO.getAvailableRunnersNow();

        for (Runner runner : availableRunners) {
            request.setAssignedRunnerId(runner.getId());
            System.out.println("✅ Assigned runner (runner_id=" + runner.getId() + ") to request");

            boolean inserted = serviceDAO.insertRequestWithRunner(request, runner.getId());

            if (inserted) {
                return RunnerAssignmentDAO.insertRunnerAssignment(
                        runner.getId(),
                        request.getTaskDescription(),
                        request.getPickupAddress() + " to " + request.getDeliveryAddress(),
                        "Assigned"
                );
            }

            return false; // request insert failed
        }

        // ❌ No available runner now
        System.out.println("❌ No available runners - request submitted without assignment");
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
