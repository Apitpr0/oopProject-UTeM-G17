package controller;

import dao.ServiceDAO;
import dao.RunnerDAO;
import dao.RunnerAssignmentDAO;
import model.RunnerStats;
import model.ServiceRequest;
import model.Runner;
import util.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<Runner, RunnerStats> getRunnerPerformanceWithRatings() {
        Map<Runner, RunnerStats> map = new HashMap<>();

        String sql = """
    SELECT\s
        cr.assigned_runner_id AS runner_id,
        u.name,
        u.email,
        COUNT(cr.id) AS completed_tasks,
        COALESCE(AVG(r.rating), 0) AS avg_rating
    FROM cust_request cr
    JOIN users u ON cr.assigned_runner_id = u.id
    LEFT JOIN ratings r ON cr.id = r.task_id
    WHERE cr.status = 'completed'
    GROUP BY cr.assigned_runner_id, u.name, u.email
    ORDER BY completed_tasks DESC,avg_rating DESC
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int runnerId = rs.getInt("runner_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int completedTasks = rs.getInt("completed_tasks");
                double avgRating = rs.getDouble("avg_rating");

                Runner runner = new Runner(
                        runnerId,
                        name,
                        email != null ? email : "",
                        "",
                        "-",
                        "-",
                        "-",
                        0
                );

                RunnerStats stats = new RunnerStats(completedTasks, avgRating);
                map.put(runner, stats);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving runner performance with ratings: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }

    public String getErrandStatus(int requestId, String task) {
        return RunnerAssignmentDAO.getStatusByRunnerAndTask(requestId, task);
    }


    // Alternative: If you want to rank runners by performance score
    public static Map < Runner, RunnerStats > getTopPerformingRunners() {
        Map < Runner, RunnerStats > map = new LinkedHashMap < > (); // Preserve order

        String sql = """
    SELECT\s
        cr.assigned_runner_id AS runner_id,
        u.name,
        u.email,
        COUNT(cr.id) AS completed_tasks,
        COALESCE(AVG(r.rating), 0) AS avg_rating
    FROM cust_request cr
    JOIN users u ON cr.assigned_runner_id = u.id
    LEFT JOIN ratings r ON cr.id = r.task_id
    WHERE cr.status = 'completed'
    GROUP BY cr.assigned_runner_id, u.name, u.email
    ORDER BY completed_tasks DESC,avg_rating DESC
    """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int runnerId = rs.getInt("runner_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int completedTasks = rs.getInt("completed_tasks");
                double avgRating = rs.getDouble("avg_rating");

                Runner runner = new Runner(
                        runnerId,
                        name,
                        email != null ? email : "",
                        "",
                        "-",
                        "-",
                        "-",
                        0
                );

                RunnerStats stats = new RunnerStats(completedTasks, avgRating);
                map.put(runner, stats);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving top performing runners: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }
}