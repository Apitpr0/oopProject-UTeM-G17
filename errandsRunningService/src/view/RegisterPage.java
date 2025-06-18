package view;

import controller.UserController;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 80, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(120, 20, 200, 25);
        add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 60, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(120, 60, 200, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 100, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 100, 200, 25);
        add(passwordField);

        passwordStrengthBar = new JProgressBar(0, 100);
        passwordStrengthBar.setBounds(120, 130, 200, 10);
        passwordStrengthBar.setStringPainted(false);
        add(passwordStrengthBar);

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStrength(); }
            public void removeUpdate(DocumentEvent e) { updateStrength(); }
            public void changedUpdate(DocumentEvent e) { updateStrength(); }

            private void updateStrength() {
                String password = new String(passwordField.getPassword());
                int strength = calculateStrength(password);
                passwordStrengthBar.setValue(strength);
                if (strength < 40) {
                    passwordStrengthBar.setForeground(Color.RED);
                } else if (strength < 70) {
                    passwordStrengthBar.setForeground(Color.ORANGE);
                } else {
                    passwordStrengthBar.setForeground(Color.GREEN);
                }
            }

            private int calculateStrength(String password) {
                int score = 0;
                if (password.length() >= 6) score += 20;
                if (password.matches(".*[A-Z].*")) score += 20;
                if (password.matches(".*[a-z].*")) score += 20;
                if (password.matches(".*\\d.*")) score += 20;
                if (password.matches(".*[^A-Za-z0-9].*")) score += 20;
                return score;
            }
        });

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(20, 160, 80, 25);
        add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"Customer", "Runner"});
        roleComboBox.setBounds(120, 160, 200, 25);
        add(roleComboBox);

        JLabel runnerPasswordLabel = new JLabel("Runner Password:");
        runnerPasswordLabel.setBounds(20, 200, 120, 25);
        add(runnerPasswordLabel);

        runnerPasswordField = new JPasswordField();
        runnerPasswordField.setBounds(150, 200, 170, 25);
        runnerPasswordField.setVisible(false);
        runnerPasswordLabel.setVisible(false);
        add(runnerPasswordField);

        roleComboBox.addActionListener(e -> {
            boolean isRunner = roleComboBox.getSelectedItem().toString().equalsIgnoreCase("runner");
            runnerPasswordField.setVisible(isRunner);
            runnerPasswordLabel.setVisible(isRunner);
        });

        registerButton = new JButton("Register");
        registerButton.setBounds(120, 250, 100, 30);
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
