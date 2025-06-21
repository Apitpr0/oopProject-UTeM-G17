package view;

import model.User;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {
    private User user;

    public CustomerDashboard(User user) {
        this.user = user;

        setTitle("Customer Dashboard - Welcome " + user.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + " (Customer)!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.CENTER);

        setVisible(true);
    }
}
