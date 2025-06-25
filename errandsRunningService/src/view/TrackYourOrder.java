package view;

import controller.ServiceController;
import model.Task;
import model.Runner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TrackYourOrder extends JFrame {
    private final int taskId;
    private final ServiceController serviceController;
    private final Runnable refreshCallback;

    // UI Components
    private JLabel taskLabel;
    private JLabel pickupLabel;
    private JLabel deliveryLabel;
    private JLabel statusLabel;
    private JLabel runnerLabel;

    public TrackYourOrder(int taskId, Runnable refreshCallback) {
        this.taskId = taskId;
        this.serviceController = new ServiceController();
        this.refreshCallback = refreshCallback;

        // Verify order exists before UI initialization
        if (!verifyOrderExists()) {
            return; // Exit if order doesn't exist
        }

        initializeUI();
        refreshOrderDetails();
    }

    private boolean verifyOrderExists() {
        if (!serviceController.doesOrderExist(taskId)) {
            JOptionPane.showMessageDialog(null,
                    "Order #" + taskId + " not found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return false;
        }
        return true;
    }

    private void initializeUI() {
        setTitle("Track Your Order - Task #" + taskId);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 250, 250)); // Light background

        // Create details panel
        JPanel orderDetailsPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        orderDetailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        orderDetailsPanel.setBackground(new Color(255, 255, 255));

        taskLabel = createStyledLabel();
        pickupLabel = createStyledLabel();
        deliveryLabel = createStyledLabel();
        statusLabel = createStyledLabel();
        runnerLabel = createStyledLabel();

        orderDetailsPanel.add(taskLabel);
        orderDetailsPanel.add(pickupLabel);
        orderDetailsPanel.add(deliveryLabel);
        orderDetailsPanel.add(statusLabel);
        orderDetailsPanel.add(runnerLabel);

        add(orderDetailsPanel, BorderLayout.CENTER);

        // Add refresh button (Green)
        JButton refreshButton = new JButton("Refresh Status");
        refreshButton.setBackground(new Color(34, 193, 195)); // Green color
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(150, 40));
        refreshButton.addActionListener(e -> refreshOrderDetails());

        // Hover effect for the green button
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(30, 150, 150)); // Hover darker green
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(34, 193, 195)); // Original green
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add window listener for callback
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            }
        });
    }

    private JLabel createStyledLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80)); // Dark grey color for text
        return label;
    }

    private void refreshOrderDetails() {
        Task order = serviceController.getOrderDetailsById(taskId);
        if (order == null || !verifyOrderExists()) {
            return; // Exit if order became invalid
        }

        Runner runner = null;
        if (order.getRunnerId() > 0) {
            runner = serviceController.getRunnerById(order.getRunnerId());
        }

        // Update labels with dynamic data
        taskLabel.setText("<html><strong>Task:</strong> " + order.getTaskDescription() + "</html>");
        pickupLabel.setText("<html><strong>Pickup:</strong> " + order.getPickupAddress() + "</html>");
        deliveryLabel.setText("<html><strong>Delivery:</strong> " + order.getDeliveryAddress() + "</html>");
        statusLabel.setText("<html><strong>Status:</strong> " + order.getStatus() + "</html>");
        runnerLabel.setText("<html><strong>Runner:</strong> " + (runner != null ? runner.getName() : "Not assigned") + "</html>");
    }
}
