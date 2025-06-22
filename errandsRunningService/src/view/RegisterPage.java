package view;

import controller.UserController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class RegisterPage extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JPasswordField runnerPasswordField;
    private JProgressBar passwordStrengthBar;
    private JButton registerButton;
    private String registeredEmail;
    private RegistrationListener registrationListener;

    private UserController controller;

    public RegisterPage() {
        controller = new UserController();

        setTitle("Register Page");
        setSize(480, 500);  // ⬆ Increased height for space
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 20, 250, 25);
        add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 60, 100, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 60, 250, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 100, 100, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 250, 25);
        add(passwordField);

        passwordStrengthBar = new JProgressBar(0, 100);
        passwordStrengthBar.setBounds(150, 130, 250, 10);
        passwordStrengthBar.setStringPainted(false);
        add(passwordStrengthBar);

        // 🟢 Password Requirements with Colored Text
        JLabel lengthLabel = new JLabel("At least 8 characters");
        JLabel upperLabel = new JLabel("Contains uppercase letter");
        JLabel lowerLabel = new JLabel("Contains lowercase letter");
        JLabel numberLabel = new JLabel("Contains number");
        JLabel symbolLabel = new JLabel("Contains special character");

        JLabel[] ruleLabels = {lengthLabel, upperLabel, lowerLabel, numberLabel, symbolLabel};
        int yStart = 145;
        for (int i = 0; i < ruleLabels.length; i++) {
            JLabel label = ruleLabels[i];
            label.setBounds(150, yStart + i * 18, 300, 18);
            label.setFont(new Font("Arial", Font.PLAIN, 11));
            label.setForeground(Color.RED);  // Default red
            add(label);
        }

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFeedback(); }
            public void removeUpdate(DocumentEvent e) { updateFeedback(); }
            public void changedUpdate(DocumentEvent e) { updateFeedback(); }

            private void updateFeedback() {
                String pwd = new String(passwordField.getPassword());

                // Strength bar
                int score = 0;
                if (pwd.length() >= 6) score += 20;
                if (pwd.matches(".*[A-Z].*")) score += 20;
                if (pwd.matches(".*[a-z].*")) score += 20;
                if (pwd.matches(".*\\d.*")) score += 20;
                if (pwd.matches(".*[^A-Za-z0-9].*")) score += 20;

                passwordStrengthBar.setValue(score);
                passwordStrengthBar.setForeground(
                        score < 40 ? Color.RED : score < 70 ? Color.ORANGE : Color.GREEN
                );

                // Requirement color updates
                lengthLabel.setForeground(pwd.length() >= 8 ? Color.GREEN : Color.RED);
                upperLabel.setForeground(pwd.matches(".*[A-Z].*") ? Color.GREEN : Color.RED);
                lowerLabel.setForeground(pwd.matches(".*[a-z].*") ? Color.GREEN : Color.RED);
                numberLabel.setForeground(pwd.matches(".*\\d.*") ? Color.GREEN : Color.RED);
                symbolLabel.setForeground(pwd.matches(".*[^A-Za-z0-9].*") ? Color.GREEN : Color.RED);
            }
        });

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(20, 250, 100, 25);
        add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"Customer", "Runner"});
        roleComboBox.setBounds(150, 250, 250, 25);
        add(roleComboBox);

        JLabel runnerPasswordLabel = new JLabel("Runner Password:");
        runnerPasswordLabel.setBounds(20, 290, 120, 25);
        add(runnerPasswordLabel);

        runnerPasswordField = new JPasswordField();
        runnerPasswordField.setBounds(150, 290, 250, 25);
        runnerPasswordField.setVisible(false);
        runnerPasswordLabel.setVisible(false);
        add(runnerPasswordField);

        roleComboBox.addActionListener(e -> {
            boolean isRunner = roleComboBox.getSelectedItem().toString().equalsIgnoreCase("runner");
            runnerPasswordField.setVisible(isRunner);
            runnerPasswordLabel.setVisible(isRunner);
        });

        registerButton = new JButton("Register");
        registerButton.setBounds(180, 340, 100, 30);
        add(registerButton);

        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = roleComboBox.getSelectedItem().toString().toLowerCase();
            String runnerPassword = new String(runnerPasswordField.getPassword());

            try {
                String errorMsg = controller.getRegisterError(name, email, password, role, runnerPassword);
                if (errorMsg == null) {
                    boolean success = controller.registerUser(name, email, password, role);
                    if (success) {
                        registeredEmail = email;
                        String successMsg = "Registration successful for " + email;

                        if (registrationListener != null) {
                            registrationListener.onRegistrationComplete(successMsg);
                        }

                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    public void setRegistrationListener(RegistrationListener listener) {
        this.registrationListener = listener;
    }

    public String getRegisteredEmail() {
        return registeredEmail;
    }

    public interface RegistrationListener {
        void onRegistrationComplete(String resultMessage);
    }
}
