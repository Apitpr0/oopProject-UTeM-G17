package controller;

import dao.ServiceDAO;
import dao.RunnerDAO;
import dao.RunnerAssignmentDAO;
import model.RunnerStats;
import model.ServiceRequest;
import model.Runner;
import util.DBConnection;

import java.sql.*;
import java.util.*;

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
        u.id AS runner_id,
        u.name,
        u.email,
        COUNT(DISTINCT cr.id) AS completed_tasks,
        COALESCE((
            SELECT AVG(r.rating)
            FROM ratings r
            WHERE r.runner_id = u.id
            ), 0) AS avg_rating
    FROM users u
    JOIN runner_assignments ra ON u.id = ra.runner_id
    JOIN cust_request cr ON cr.assigned_runner_id
    JOIN tasks t ON t.request_id = cr.id
    WHERE ra.status = 'Completed' AND t.status = 'arrived'
    GROUP BY u.id, u.name, u.email
    ORDER BY completed_tasks DESC, avg_rating DESC;
    
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


    // Alternative: If you want to rank runners by performance score
    public static Map < Runner, RunnerStats > getTopPerformingRunners() {
        Map < Runner, RunnerStats > map = new LinkedHashMap < > (); // Preserve order

        String sql = """
    SELECT\s
        u.id AS runner_id,
        u.name,
        u.email,
        COUNT(DISTINCT cr.id) AS completed_tasks,
        COALESCE((
            SELECT AVG(r.rating)
            FROM ratings r
            WHERE r.runner_id = u.id
            ), 0) AS avg_rating
    FROM users u
    JOIN runner_assignments ra ON u.id = ra.runner_id
    JOIN cust_request cr ON cr.assigned_runner_id
    JOIN tasks t ON t.request_id = cr.id
    WHERE ra.status = 'Completed' AND t.status = 'arrived'
    GROUP BY u.id, u.name, u.email
    ORDER BY completed_tasks DESC, avg_rating DESC;
    
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
    public List<ServiceRequest> getCompletedRequests() {
        List<ServiceRequest> completedList = new ArrayList<>();
        String sql = """
        
       SELECT t.id AS task_id, t.request_id, cr.customer_id, cr.task_description,
               cr.pickup_address, cr.delivery_address, cr.additional_charge,
               u.name AS runner_name
                FROM tasks t
                JOIN cust_request cr ON t.request_id = cr.id
                LEFT JOIN users u ON t.runner_id = u.id
                WHERE t.status = 'arrived'
        
        """;


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ServiceRequest req = new ServiceRequest();
                req.setId(rs.getInt("request_id"));  // this is request ID
                req.setCustomerId(rs.getInt("customer_id")); // ✅ correct!
                req.setTaskDescription(rs.getString("task_description")); // ✅ correct!
                req.setPickupAddress(rs.getString("pickup_address"));
                req.setDeliveryAddress(rs.getString("delivery_address"));
                req.setAdditionalCharge(rs.getDouble("additional_charge"));
                req.setRunnerName(rs.getString("runner_name"));

                completedList.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completedList;
    }

}