package controller;

import model.*;
import dao.ServiceDAO;
import dao.RunnerDAO;
import dao.RunnerAssignmentDAO;
import util.DBConnection;

import java.sql.*;
import java.util.*;

public class ServiceController {
    private final ServiceDAO serviceDAO;
    private Connection connection;

    public ServiceController() {
        this.serviceDAO = new ServiceDAO();
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
        }
    }

    // New method to check order existence
    public boolean doesOrderExist(int taskId) {
        String sql = "SELECT 1 FROM cust_request WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking order existence: " + e.getMessage());
            return false;
        }
    }

    // Enhanced request submission methods
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

    // Request retrieval methods
    public List<ServiceRequest> getRequestsByCustomer(int customerId) {
        return serviceDAO.getRequestsByCustomer(customerId);
    }

    public ServiceRequest getRequestById(int requestId) {
        return serviceDAO.getRequestById(requestId);
    }

    public Task getOrderDetailsById(int taskId) {
        String sql = "SELECT * FROM cust_request WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Task(
                            rs.getInt("id"),
                            rs.getString("status"),
                            rs.getInt("customer_id"),
                            rs.getInt("assigned_runner_id"),
                            getRunnerNameByRequestId(taskId),
                            rs.getString("task_description"),
                            rs.getString("pickup_address"),
                            rs.getString("delivery_address"),
                            rs.getString("urgency"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order details: " + e.getMessage());
        }
        return null;
    }

    // Runner-related methods
    public String getRunnerNameByRequestId(int requestId) {
        String sql = "SELECT u.name FROM users u JOIN cust_request r ON u.id = r.assigned_runner_id WHERE r.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    public Runner getRunnerById(int runnerId) {
        String sql = "SELECT u.id, u.name, u.email FROM users u WHERE u.id = ? AND u.role = 'runner'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, runnerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Runner(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "",
                        "",
                        "",
                        "",
                        0
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching runner by ID: " + e.getMessage());
        }
        return null;
    }

    // Task management methods
    public Task getTaskById(int taskId) {
        String sql = """
            SELECT t.id, t.customer_id, t.runner_id, t.status, t.updated_at, t.request_id,
                   cr.task_description, cr.pickup_address, cr.delivery_address, cr.urgency,
                   u.name AS runner_name
            FROM tasks t
            JOIN cust_request cr ON t.request_id = cr.id
            LEFT JOIN users u ON t.runner_id = u.id
            WHERE t.id = ?
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        }
        return null;
    }

    public List<Task> getCustomerTasks(int customerId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.id, t.customer_id, t.runner_id, t.status, t.updated_at, t.request_id,
                   cr.task_description, cr.pickup_address, cr.delivery_address, cr.urgency,
                   u.name AS runner_name
            FROM tasks t
            JOIN cust_request cr ON t.request_id = cr.id
            LEFT JOIN users u ON t.runner_id = u.id
            WHERE t.customer_id = ?
            ORDER BY t.updated_at DESC
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        }
        return tasks;
    }

    // User management methods
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, role FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
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

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete user: " + e.getMessage());
        }
        return false;
    }

    // Performance and statistics methods
    public static Map<Runner, RunnerStats> getRunnerPerformanceWithRatings() {
        Map<Runner, RunnerStats> map = new HashMap<>();
        String sql = """
            SELECT
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
            JOIN cust_request cr ON cr.assigned_runner_id = u.id
            JOIN tasks t ON t.request_id = cr.id
            WHERE ra.status = 'Completed' AND t.status = 'arrived'
            GROUP BY u.id, u.name, u.email
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
                        "-",
                        "-",
                        "-",
                        0
                );
                RunnerStats stats = new RunnerStats(
                        rs.getInt("completed_tasks"),
                        rs.getDouble("avg_rating")
                );
                map.put(runner, stats);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving runner performance: " + e.getMessage());
        }
        return map;
    }

    public static Map<Runner, RunnerStats> getTopPerformingRunners() {
        Map<Runner, RunnerStats> map = new LinkedHashMap<>();
        String sql = """
            SELECT
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
            JOIN cust_request cr ON cr.assigned_runner_id = u.id
            JOIN tasks t ON t.request_id = cr.id
            WHERE ra.status = 'Completed' AND t.status = 'arrived'
            GROUP BY u.id, u.name, u.email
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
                        "-",
                        "-",
                        "-",
                        0
                );
                RunnerStats stats = new RunnerStats(
                        rs.getInt("completed_tasks"),
                        rs.getDouble("avg_rating")
                );
                map.put(runner, stats);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top performing runners: " + e.getMessage());
        }
        return map;
    }

    // Completed requests and payment status
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
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
            System.err.println("Error retrieving completed requests: " + e.getMessage());
        }
        return completedList;
    }

    public String getPaymentStatus(int requestId) {
        String query = "SELECT p.payment_status FROM payments p " +
                "JOIN cust_request cr ON p.quote_id = cr.id " +
                "WHERE cr.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("payment_status");
            }
        } catch (SQLException e) {
            System.err.println("Error getting payment status: " + e.getMessage());
        }
        return "Unpaid";
    }

    public int getLatestRequestId(int customerId) {
        String query = "SELECT id FROM cust_request " +
                "WHERE customer_id = ? " +
                "ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting latest request ID: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateRequestStatus(int requestId, String status) {
        String query = "UPDATE cust_request SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            return false;
        }
    }

    // Cleanup method
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}