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
import java.text.DecimalFormat;
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

        // Top Panel: Welcome + Logout
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
                new LoginPage("someTitle").setVisible(true); // Provide an appropriate title
            }
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // My Requests Tab
        JPanel requestPanel = new JPanel(new BorderLayout(10, 10));
        requestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] requestColumns = {"Request ID", "Task", "Pickup", "Delivery", "Urgency", "Status", "Charge (RM)", "Assigned Runner"};
        requestTableModel = new DefaultTableModel(requestColumns, 0);
        requestTable = new JTable(requestTableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        requestPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        tabbedPane.addTab("My Requests", requestPanel);

        // Runner Availability Tab
        JPanel runnerPanel = new JPanel(new BorderLayout(10, 10));
        runnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] runnerColumns = {"Runner ID", "Name", "Day Available", "Start Time", "End Time", "Rating"};
        runnerTableModel = new DefaultTableModel(runnerColumns, 0);
        runnerTable = new JTable(runnerTableModel);
        runnerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        runnerPanel.add(new JScrollPane(runnerTable), BorderLayout.CENTER);
        tabbedPane.addTab("Runner Availability", runnerPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Form Panel
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

        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Task Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Pickup Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pickupField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Delivery Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deliveryField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(urgentBox, gbc);

        // Track Your Order button (left side)
        JButton trackOrderButton = new JButton("Track Your Order");
        trackOrderButton.setPreferredSize(new Dimension(180, 30));
        JPanel trackOrderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        trackOrderPanel.add(trackOrderButton);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(trackOrderPanel, gbc);

        // Submit button (right side)
        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30));
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitPanel.add(submitButton);
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(submitPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Submit Logic
        submitButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            String pickup = pickupField.getText().trim();
            String delivery = deliveryField.getText().trim();
            boolean isUrgent = urgentBox.isSelected();

            if (task.isEmpty() || pickup.isEmpty() || delivery.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            double[] quoteDetails = calculateQuote(pickup, delivery, isUrgent);
            DecimalFormat df = new DecimalFormat("0.00");

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Quote Details:\n" +
                            "Distance: " + String.format("%.1f", quoteDetails[0]) + " km\n" +
                            "Base Fee: RM" + df.format(quoteDetails[1]) + "\n" +
                            "Distance Fee: RM" + df.format(quoteDetails[2]) + "\n" +
                            (isUrgent ? "Urgency Surcharge: RM10.00\n" : "") +
                            "Tax (10%): RM" + df.format(quoteDetails[3]) + "\n" +
                            "--------------------------\n" +
                            "Total: RM" + df.format(quoteDetails[4]) + "\n\n" +
                            "Do you want to proceed?",
                    "Quote Preview", JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                ServiceRequest request = isUrgent
                        ? new UrgentServiceRequest(customer.getId(), task, pickup, delivery)
                        : new ServiceRequest(customer.getId(), task, pickup, delivery);

                boolean success = serviceController.submitRequestWithRunnerAssignment(request);
                if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Request submitted!");
                    taskField.setText("");
                    pickupField.setText("");
                    deliveryField.setText("");
                    urgentBox.setSelected(false);
                    refreshRequestTable();
                    refreshRunnerTable();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to submit request.");
                }
            }
        });


    // Track Your Order Button Functionality (opens new pop-up window)
        trackOrderButton.addActionListener(e -> {
            int selectedRow = requestTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a request to track");
                return;
            }

            // Get the taskId from the selected row
            int taskId = (int) requestTableModel.getValueAt(selectedRow, 0);

            // Add validation to ensure the task exists
            ServiceRequest request = serviceController.getRequestById(taskId);
            if (request == null) {
                JOptionPane.showMessageDialog(this, "Request not found!");
                return;
            }

            // Open TrackYourOrder window
            new TrackYourOrder(taskId, this::refreshRequestTable).setVisible(true);
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

    private double[] calculateQuote(String pickup, String delivery, boolean isUrgent) {
        double baseFee = 5.00;
        double perKmRate = 1.50;
        double urgentCharge = 10.00;
        double taxRate = 0.10;

        double distance = calculateDistance(pickup, delivery);
        double distanceFee = distance * perKmRate;
        double subtotal = baseFee + distanceFee + (isUrgent ? urgentCharge : 0);
        double tax = subtotal * taxRate;
        double total = subtotal + tax;

        return new double[]{distance, baseFee, distanceFee, tax, total};
    }

    private double calculateDistance(String pickup, String delivery) {
        return 2 + Math.random() * 18; // 2–20km range
    }
}
