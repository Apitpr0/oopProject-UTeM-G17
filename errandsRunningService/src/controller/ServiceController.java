package controller;

import dao.ServiceDAO;
import dao.RunnerDAO;
import dao.RunnerAssignmentDAO;
import model.RunnerStats;
import model.ServiceRequest;
import model.Runner;
import model.Task;
import util.DBConnection;

import java.sql.*;
import java.util.*;

public class ServiceController {
    private final ServiceDAO serviceDAO;

    public ServiceController() {
        this.serviceDAO = new ServiceDAO();
    }

    // ==================== ORDER/REQUEST METHODS ====================

    public boolean doesOrderExist(int taskId) {
        String sql = "SELECT 1 FROM cust_request WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking order existence: " + e.getMessage());
            return false;
        }
    }

    public Task getOrderDetailsById(int taskId) {
        String sql = """
            SELECT 
                cr.id,
                cr.customer_id,
                cr.task_description,
                COALESCE(t.status, cr.status) AS status,
                cr.pickup_address,
                cr.delivery_address,
                cr.urgency,
                cr.additional_charge,
                cr.assigned_runner_id,
                u.name AS runner_name,
                COALESCE(t.updated_at, cr.created_at) AS last_updated
            FROM cust_request cr
            LEFT JOIN tasks t ON t.request_id = cr.id
            LEFT JOIN users u ON cr.assigned_runner_id = u.id
            WHERE cr.id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Task(
                            rs.getInt("id"),
                            rs.getString("status"),
                            rs.getInt("customer_id"),
                            rs.getInt("assigned_runner_id"),
                            rs.getString("runner_name"),
                            rs.getString("task_description"),
                            rs.getString("pickup_address"),
                            rs.getString("delivery_address"),
                            rs.getString("urgency"),
                            rs.getTimestamp("last_updated")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getCurrentStatus(int requestId) {
        String sql = """
            SELECT COALESCE(
                (SELECT status FROM tasks WHERE request_id = ? ORDER BY updated_at DESC LIMIT 1),
                (SELECT status FROM cust_request WHERE id = ?)
            ) AS current_status
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.setInt(2, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("current_status");
            }
        } catch (SQLException e) {
            System.err.println("Error getting current status: " + e.getMessage());
        }
        return "Submitted";
    }

    public ServiceRequest getRequestById(int requestId) {
        return serviceDAO.getRequestById(requestId);
    }

    public boolean submitRequest(ServiceRequest request) {
        return serviceDAO.insertRequest(request);
    }

    public boolean submitRequestWithRunnerAssignment(ServiceRequest request) {
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

            return false;
        }

        System.out.println("❌ No available runners - request submitted without assignment");
        return serviceDAO.insertRequest(request);
    }

    public List<ServiceRequest> getRequestsByCustomer(int customerId) {
        return serviceDAO.getRequestsByCustomer(customerId);
    }

    // ==================== RUNNER METHODS ====================

    public Runner getRunnerById(int runnerId) {
        String sql = """
            SELECT u.id, u.name, u.email, u.rating
            FROM users u 
            WHERE u.id = ? AND u.role = 'runner'
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, runnerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "",  // phone
                        "",  // vehicle_type
                        "",  // license_plate
                        "",  // availability
                        rs.getInt("rating")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching runner by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getRunnerNameByRequestId(int requestId) {
        String sql = """
            SELECT u.name 
            FROM users u 
            JOIN cust_request r ON u.id = r.assigned_runner_id 
            WHERE r.id = ?
            """;

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

    // ==================== TASK METHODS ====================

    public Task getTaskById(int taskId) {
        String sql = """
            SELECT 
                t.id, 
                t.customer_id, 
                t.runner_id, 
                t.status, 
                t.updated_at, 
                t.request_id,
                cr.task_description, 
                cr.pickup_address, 
                cr.delivery_address, 
                cr.urgency,
                cr.additional_charge,
                u.name AS runner_name
            FROM tasks t
            JOIN cust_request cr ON t.request_id = cr.id
            LEFT JOIN users u ON t.runner_id = u.id
            WHERE t.id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Task(
                        rs.getInt("id"),
                        rs.getString("status"),
                        rs.getInt("customer_id"),
                        rs.getInt("runner_id"),
                        rs.getString("runner_name"),
                        rs.getString("task_description"),
                        rs.getString("pickup_address"),
                        rs.getString("delivery_address"),
                        rs.getString("urgency"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching task by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Task> getCustomerTasks(int customerId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT 
                t.id, 
                t.customer_id, 
                t.runner_id, 
                t.status, 
                t.updated_at, 
                t.request_id,
                cr.task_description, 
                cr.pickup_address, 
                cr.delivery_address, 
                cr.urgency,
                cr.additional_charge,
                u.name AS runner_name
            FROM tasks t
            JOIN cust_request cr ON t.request_id = cr.id
            LEFT JOIN users u ON t.runner_id = u.id
            WHERE t.customer_id = ?
            ORDER BY t.updated_at DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("status"),
                        rs.getInt("customer_id"),
                        rs.getInt("runner_id"),
                        rs.getString("runner_name"),
                        rs.getString("task_description"),
                        rs.getString("pickup_address"),
                        rs.getString("delivery_address"),
                        rs.getString("urgency"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer tasks: " + e.getMessage());
            e.printStackTrace();
        }
        return tasks;
    }

    // ==================== USER MANAGEMENT METHODS ====================

    public List<model.User> getAllUsers() {
        List<model.User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, role FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                model.User user = new model.User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to get users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateUser(model.User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to update user: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to delete user: " + e.getMessage());
        }
        return false;
    }

    // ==================== PERFORMANCE & REPORTING METHODS ====================

    public static Map<Runner, RunnerStats> getRunnerPerformanceWithRatings() {
        Map<Runner, RunnerStats> map = new HashMap<>();

        String sql = """
            SELECT 
                u.id AS runner_id,
                u.name,
                u.email,
                u.rating,
                COUNT(DISTINCT cr.id) AS completed_tasks,
                COALESCE((
                    SELECT AVG(r.rating)
                    FROM ratings r
                    WHERE r.runner_id = u.id
                    ), 0) AS avg_rating
            FROM users u
            LEFT JOIN cust_request cr ON u.id = cr.assigned_runner_id
            LEFT JOIN tasks t ON t.request_id = cr.id
            WHERE u.role = 'runner' AND (t.status = 'arrived' OR t.status IS NULL)
            GROUP BY u.id, u.name, u.email, u.rating
            ORDER BY completed_tasks DESC, avg_rating DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Runner runner = new Runner(
                        rs.getInt("runner_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "",
                        "",
                        "",
                        "",
                        rs.getInt("rating")
                );

                RunnerStats stats = new RunnerStats(
                        rs.getInt("completed_tasks"),
                        rs.getDouble("avg_rating")
                );
                map.put(runner, stats);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving runner performance: " + e.getMessage());
            e.printStackTrace();
        }

        return map;
    }

    public List<ServiceRequest> getCompletedRequests() {
        List<ServiceRequest> completedList = new ArrayList<>();
        String sql = """
            SELECT 
                t.id AS task_id, 
                t.request_id, 
                cr.customer_id, 
                cr.task_description,
                cr.pickup_address, 
                cr.delivery_address, 
                cr.additional_charge,
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
                req.setId(rs.getInt("request_id"));
                req.setCustomerId(rs.getInt("customer_id"));
                req.setTaskDescription(rs.getString("task_description"));
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

    // ==================== UTILITY METHODS ====================

    public int getRequestIdFromDescription(String description) {
        String sql = "SELECT id FROM cust_request WHERE task_description = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, description);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting request ID: " + e.getMessage());
        }

        return -1;
    }

    public String getErrandStatus(int requestId, String task) {
        return RunnerAssignmentDAO.getStatusByRunnerAndTask(requestId, task);
    }
}