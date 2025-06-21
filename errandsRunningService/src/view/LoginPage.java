package view;

import controller.UserController;
import model.Customer;
import model.Runner;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserController controller;

    public LoginPage(String prefillEmail) {
        controller = new UserController();

        setTitle("Login Page");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null); // Center window

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 30, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 30, 180, 25);
        emailField.setText(prefillEmail);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 70, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 180, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(150, 110, 80, 30);
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(240, 110, 90, 30);
        add(registerButton);

        // Login button action
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            String errorMsg = controller.getLoginError(email, password);
            if (errorMsg != null) {
                JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = controller.login(email, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Welcome, " + user.getName());
                dispose(); // Close login window

                if (user instanceof Customer) {
                    SwingUtilities.invokeLater(() -> new CustomerDashboard((Customer) user));
                }else if (user instanceof Runner) {
                    SwingUtilities.invokeLater(() -> new RunnerDashboard(user)); // Open the real RunnerDashboard
                }else {
                    JOptionPane.showMessageDialog(this, "Unknown role.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Register button action
        registerButton.addActionListener(e -> {
            RegisterPage registerPage = new RegisterPage();
            registerPage.setRegistrationListener(resultMessage -> {
                JOptionPane.showMessageDialog(this, resultMessage);
                String registeredEmail = registerPage.getRegisteredEmail();
                dispose(); // Close current login page
                SwingUtilities.invokeLater(() -> new LoginPage(registeredEmail));
            });
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage(""));
    }
}
