package dao;

import model.Customer;
import model.Runner;
import model.User;
import util.DBConnection;
import util.SecurityUtil;

import java.sql.*;

public class UserDAO {

    public static User login(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                String inputHash = SecurityUtil.hashPassword(password);

                if (inputHash.equals(hashedPassword)) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    String availability = rs.getString("availability"); // ✅ added

                    if (role.equalsIgnoreCase("customer")) {
                        return new Customer(id, name, email, hashedPassword);
                    } else if (role.equalsIgnoreCase("runner")) {
                        return new Runner(id, name, email, hashedPassword, availability);
                    } else {
                        System.out.println("⚠️ Unknown role: " + role);
                    }
                } else {
                    System.out.println("❌ Incorrect password.");
                }
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }

        return null;
    }

    public static boolean register(String name, String email, String password, String role) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("⚠️ Name cannot be empty.");
            return false;
        }

        if (!SecurityUtil.isValidEmail(email)) {
            System.out.println("⚠️ Invalid email format.");
            return false;
        }

        if (!SecurityUtil.isStrongPassword(password)) {
            System.out.println("⚠️ Weak password. Must include upper, lower, digit, and special char.");
            return false;
        }

        String hashedPassword = SecurityUtil.hashPassword(password);
        String query = "INSERT INTO users (name, email, password, role, availability) VALUES (?, ?, ?, ?, NULL)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role.toLowerCase());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠️ Email already exists.");
        } catch (SQLException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }

        return false;
    }

    public static boolean changePassword(String email, String currentPassword, String newPassword) {
        String query = "SELECT password FROM users WHERE email = ?";
        String update = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(query)) {

            selectStmt.setString(1, email);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String currentHash = rs.getString("password");
                if (!SecurityUtil.hashPassword(currentPassword).equals(currentHash)) {
                    System.out.println("❌ Current password is incorrect.");
                    return false;
                }

                if (!SecurityUtil.isStrongPassword(newPassword)) {
                    System.out.println("⚠️ New password is too weak.");
                    return false;
                }

                String newHashed = SecurityUtil.hashPassword(newPassword);

                try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                    updateStmt.setString(1, newHashed);
                    updateStmt.setString(2, email);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Password change failed: " + e.getMessage());
        }

        return false;
    }

    public static boolean resetPassword(String email, String newPassword) {
        if (!SecurityUtil.isStrongPassword(newPassword)) {
            System.out.println("⚠️ New password is too weak.");
            return false;
        }

        String hashed = SecurityUtil.hashPassword(newPassword);
        String update = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {

            stmt.setString(1, hashed);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Reset failed: " + e.getMessage());
        }

        return false;
    }

    // ✅ NEW: Update User's Name (Profile Editing)
    public static boolean updateName(int userId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            System.out.println("⚠️ New name cannot be empty.");
            return false;
        }

        String update = "UPDATE users SET name = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {

            stmt.setString(1, newName.trim());
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("❌ Failed to update name: " + e.getMessage());
        }

        return false;
    }
}
