package view;

import controller.ServiceController;
import controller.RunnerController;
import model.Customer;
import model.ServiceRequest;
import model.UrgentServiceRequest;
import model.Runner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private Customer customer;
    private JTable requestTable;
    private JTable runnerTable;
    private DefaultTableModel requestTableModel;
    private DefaultTableModel runnerTableModel;
    private ServiceController serviceController;
    private RunnerController runnerController;

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        this.serviceController = new ServiceController();
        this.runnerController = new RunnerController();

        setTitle("Customer Dashboard - Welcome, " + customer.getName());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top title
        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Center: Two tables side by side
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        // Request Table
        String[] requestColumns = {"Request ID", "Task", "Pickup", "Delivery", "Urgency", "Status", "Charge (RM)", "Assigned Runner"};
        requestTableModel = new DefaultTableModel(requestColumns, 0);
        requestTable = new JTable(requestTableModel);
        centerPanel.add(new JScrollPane(requestTable));


        // Runner Availability Table
        String[] runnerColumns = {"Runner ID", "Name", "Day Available", "Start Time", "End Time", "Rating"};
        runnerTableModel = new DefaultTableModel(runnerColumns, 0);
        runnerTable = new JTable(runnerTableModel);
        centerPanel.add(new JScrollPane(runnerTable));

        add(centerPanel, BorderLayout.CENTER);

        // Form inputs using GridBagLayout for better alignment
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15 , 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField taskField = new JTextField(20);
        JTextField pickupField = new JTextField(20);
        JTextField deliveryField = new JTextField(20);
        JCheckBox urgentBox = new JCheckBox("Urgent (+RM10)");

        // Task row
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Task Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskField, gbc);

        // Pickup row
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Pickup Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pickupField, gbc);

        // Delivery row
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Delivery Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deliveryField, gbc);

        // Urgent checkbox
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(urgentBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30));
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(submitButton);
        buttonPanel.add(logoutButton);

        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Submit button logic
        submitButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            String pickup = pickupField.getText().trim();
            String delivery = deliveryField.getText().trim();

            if (task.isEmpty() || pickup.isEmpty() || delivery.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            boolean isUrgent = urgentBox.isSelected();
            ServiceRequest request = isUrgent
                    ? new UrgentServiceRequest(customer.getId(), task, pickup, delivery)
                    : new ServiceRequest(customer.getId(), task, pickup, delivery);

            boolean success = serviceController.submitRequestWithRunnerAssignment(request);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Request submitted and runner assigned!");
                taskField.setText("");
                pickupField.setText("");
                deliveryField.setText("");
                urgentBox.setSelected(false);
                refreshRequestTable();
                refreshRunnerTable();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to submit request.");
            }
        });

        // Log out button logic
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close window
                // new LoginScreen(); // Uncomment if login screen exists
            }
        });

        refreshRequestTable();
        refreshRunnerTable();
        setVisible(true);
    }

    private void refreshRequestTable() {
        requestTableModel.setRowCount(0);
        List<ServiceRequest> requests = serviceController.getRequestsByCustomer(customer.getId());

        for (ServiceRequest req : requests) {
            String assignedRunner = serviceController.getRunnerNameByRequestId(req.getId());
            requestTableModel.addRow(new Object[]{
                    req.getId(),
                    req.getTaskDescription(),
                    req.getPickupAddress(),
                    req.getDeliveryAddress(),
                    req.getUrgency(),
                    req.getStatus(),
                    String.format("%.2f", req.getAdditionalCharge()),
                    assignedRunner != null ? assignedRunner : "-"
            });
        }
    }

    private void refreshRunnerTable() {
        runnerTableModel.setRowCount(0);
        List<Runner> allRunners = runnerController.getAllRunnersWithAvailability();

        for (Runner runner : allRunners) {
            runnerTableModel.addRow(new Object[]{
                    runner.getId(),
                    runner.getName(),
                    runner.getDayOfWeek(),
                    runner.getStartTime(),
                    runner.getEndTime(),
                    runner.getRating()
            });
        }
    }
}
