package controller;

import dao.UserDAO;
import model.User;

public class UserController {

    public User login(String email, String password) {
        return UserDAO.login(email, password);
    }

    public String getLoginError(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email format.";
        }

        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }

        return null; // Everything looks good
    }

    public String getRegisterError(String name, String email, String password, String role, String runnerPassword) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email format.";
        }

        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }

        if (!password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[^A-Za-z0-9].*")) {
            return "Password must include upper, lower, digit, and special character.";
        }

        if (role == null || (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("runner"))) {
            return "Role must be either 'customer' or 'runner'.";
        }

        if (role.equalsIgnoreCase("runner")) {
            if (runnerPassword == null || runnerPassword.trim().isEmpty()) {
                return "Runner password is required.";
            }
            if (!runnerPassword.equals("runner123")) { // Securely store or configure in real apps
                return "Invalid runner password.";
            }
        }

        return null;
    }

    public boolean registerUser(String name, String email, String password, String role) {
        return UserDAO.register(name, email, password, role);
    }
}
