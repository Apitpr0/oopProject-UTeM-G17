import dao.UserDAO;
import model.User;

public class TestUserDAO {
    public static void main(String[] args) {
        System.out.println("=== REGISTER ===");
        boolean registered = UserDAO.register("Ali", "ali@example.com", "Password123!", "customer");
        System.out.println("Registered: " + registered);

        System.out.println("=== LOGIN ===");
        User user = UserDAO.login("ali@example.com", "Password123!");
        if (user != null) {
            System.out.println("Login successful: " + user.getName());
        }

        System.out.println("=== CHANGE PASSWORD ===");
        boolean changed = UserDAO.changePassword("ali@example.com", "Password123!", "NewPass456$");
        System.out.println("Password changed: " + changed);

        System.out.println("=== RESET PASSWORD ===");
        boolean reset = UserDAO.resetPassword("ali@example.com", "ResetPass789!");
        System.out.println("Password reset: " + reset);
    }
}
