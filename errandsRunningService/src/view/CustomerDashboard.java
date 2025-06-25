// Updated CustomerDashboard.java with enhanced table columns and centered form
package view;

import controller.ServiceController;
import controller.RunnerController;
import model.Customer;
import model.ServiceRequest;
import model.UrgentServiceRequest;
import model.Runner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
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

        // Top panel with welcome and logout
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

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // My Requests Tab
        JPanel requestPanel = new JPanel(new BorderLayout(10, 10));
        requestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] requestColumns = {"Task", "Route", "Urgency", "Charge (RM)", "Assigned Runner", "Status"};
        requestTableModel = new DefaultTableModel(requestColumns, 0);
        requestTable = new JTable(requestTableModel);

        // Colorful status renderer with emoji
        requestTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString().toLowerCase() : "";
                String emojiStatus = status;
                Color bgColor = Color.WHITE;

                switch (status) {
                    case "pending" -> {
                        emojiStatus = "⏳ Pending";
                        bgColor = Color.YELLOW;
                    }
                    case "in progress" -> {
                        emojiStatus = "🔄 In Progress";
                        bgColor = new Color(173, 216, 230);
                    }
                    case "completed" -> {
                        emojiStatus = "✅ Completed";
                        bgColor = Color.GREEN;
                    }
                    case "assigned" -> {
                        emojiStatus = "Assigned";
                    }
                }
                setText(emojiStatus);
                setBackground(isSelected ? table.getSelectionBackground() : bgColor);
                setForeground(Color.BLACK);
                return c;
            }
        });

        requestPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        tabbedPane.addTab("My Requests", requestPanel);

        // Runner Availability Tab
        JPanel runnerPanel = new JPanel(new BorderLayout(10, 10));
        runnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] runnerColumns = {"Runner ID", "Name", "Day Available", "Start Time", "End Time", "Rating"};
        runnerTableModel = new DefaultTableModel(runnerColumns, 0);
        runnerTable = new JTable(runnerTableModel);
        runnerPanel.add(new JScrollPane(runnerTable), BorderLayout.CENTER);
        tabbedPane.addTab("Runner Availability", runnerPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Submit Form
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

        JButton submitButton = new JButton("Submit");
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.add(submitButton);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(submitPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

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
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Quote Details:\nDistance: " + String.format("%.1f", quoteDetails[0]) + " km\n" +
                            "Base Fee: RM" + df.format(quoteDetails[1]) + "\n" +
                            "Distance Fee: RM" + df.format(quoteDetails[2]) + "\n" +
                            (isUrgent ? "Urgency Surcharge: RM10.00\n" : "") +
                            "Tax (10%): RM" + df.format(quoteDetails[3]) + "\n" +
                            "--------------------------\nTotal: RM" + df.format(quoteDetails[4]) +
                            "\n\nDo you want to proceed?",
                    "Quote Preview", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                ServiceRequest request = isUrgent ? new UrgentServiceRequest(customer.getId(), task, pickup, delivery)
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

        refreshRequestTable();
        refreshRunnerTable();
        setVisible(true);
    }

    private void refreshRequestTable() {
        requestTableModel.setRowCount(0);
        List<ServiceRequest> requests = serviceController.getRequestsByCustomer(customer.getId());

        for (ServiceRequest req : requests) {
            String assignedRunner = serviceController.getRunnerNameByRequestId(req.getId());
            String errandStatus = serviceController.getErrandStatus(req.getAssignedRunnerId(), req.getTaskDescription());
            String route = req.getPickupAddress() + " → " + req.getDeliveryAddress();
            requestTableModel.addRow(new Object[]{
                    req.getTaskDescription(), route, req.getUrgency(),
                    String.format("RM %.2f", req.getAdditionalCharge()),
                    assignedRunner != null ? assignedRunner : "-", errandStatus
            });
        }
    }

    private void refreshRunnerTable() {
        runnerTableModel.setRowCount(0);
        List<Runner> allRunners = runnerController.getAllRunnersWithAvailability();
        for (Runner runner : allRunners) {
            runnerTableModel.addRow(new Object[]{
                    runner.getId(), runner.getName(), runner.getDayOfWeek(),
                    runner.getStartTime(), runner.getEndTime(), runner.getRating()
            });
        }
    }

    private double[] calculateQuote(String pickup, String delivery, boolean isUrgent) {
        double baseFee = 5.00;
        double perKmRate = 1.50;
        double urgentCharge = 10.00;
        double taxRate = 0.10;

        double distance = 2 + Math.random() * 18;
        double distanceFee = distance * perKmRate;
        double subtotal = baseFee + distanceFee + (isUrgent ? urgentCharge : 0);
        double tax = subtotal * taxRate;
        double total = subtotal + tax;

        return new double[]{distance, baseFee, distanceFee, tax, total};
    }
}
