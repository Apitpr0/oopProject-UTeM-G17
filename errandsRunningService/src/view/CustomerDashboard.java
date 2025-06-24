package view;

import controller.ServiceController;
import controller.RunnerController;
import model.Customer;
import model.ServiceRequest;
import model.UrgentServiceRequest;
import model.Runner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

        // 🔝 Top Panel: Welcome + Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getName(), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginPage(""));
            }
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 📑 Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: My Requests
        JPanel requestPanel = new JPanel(new BorderLayout(10, 10));
        requestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] requestColumns = {"Request ID", "Task", "Pickup", "Delivery", "Urgency", "Status", "Charge (RM)", "Assigned Runner"};
        requestTableModel = new DefaultTableModel(requestColumns, 0);
        requestTable = new JTable(requestTableModel);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        requestPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        tabbedPane.addTab("My Requests", requestPanel);

        // Tab 2: Runner Availability
        JPanel runnerPanel = new JPanel(new BorderLayout(10, 10));
        runnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] runnerColumns = {"Runner ID", "Name", "Day Available", "Start Time", "End Time", "Rating"};
        runnerTableModel = new DefaultTableModel(runnerColumns, 0);
        runnerTable = new JTable(runnerTableModel);
        runnerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        runnerPanel.add(new JScrollPane(runnerTable), BorderLayout.CENTER);
        tabbedPane.addTab("Runner Availability", runnerPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // 📝 Form Panel at Bottom
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Submit New Request",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField taskField = new JTextField(20);
        JTextField pickupField = new JTextField(20);
        JTextField deliveryField = new JTextField(20);
        JCheckBox urgentBox = new JCheckBox("Urgent (+RM10)");

        // Task
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Task Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskField, gbc);

        // Pickup
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Pickup Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pickupField, gbc);

        // Delivery
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Delivery Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deliveryField, gbc);

        // Urgency
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(urgentBox, gbc);

        // Submit button (aligned right)
        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30));
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitPanel.add(submitButton);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(submitPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // 🧠 Submit logic
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

        // Refresh data
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
