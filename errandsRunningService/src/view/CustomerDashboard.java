package view;

import controller.*;
import model.*;
import Repository.*;
import service.*;
import util.*;
import dao.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.sql.SQLException;

public class CustomerDashboard extends JFrame {
    private Customer customer;
    private JTable requestTable;
    private JTable runnerTable;
    private DefaultTableModel requestTableModel;
    private DefaultTableModel runnerTableModel;
    private ServiceController serviceController;
    private RunnerController runnerController;
    private PaymentController paymentController;

    public CustomerDashboard(Customer customer) throws SQLException {
        this.customer = customer;
        this.serviceController = new ServiceController();
        this.runnerController = new RunnerController();
        this.paymentController = new PaymentController(
                new PaymentService(
                        new PaymentRepository(DBConnection.getConnection())
                ));
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Customer Dashboard - Welcome, " + customer.getName());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel: Welcome + Logout
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Requests", createRequestPanel());
        tabbedPane.addTab("Runner Availability", createRunnerPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // Bottom Panel: Request Form + Track Order Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(createRequestFormPanel(), BorderLayout.CENTER);

        // Add Track Order button to the bottom right
        JButton trackOrderButton = new JButton("Track Your Order");
        trackOrderButton.setPreferredSize(new Dimension(180, 30));
        trackOrderButton.addActionListener(e -> trackOrder());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(trackOrderButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshTables();
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getName(), SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.addActionListener(e -> logout());

        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Request ID", "Task", "Pickup", "Delivery", "Urgency", "Status", "Charge (RM)", "Assigned Runner", "Payment Status"};
        requestTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new JTable(requestTableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add pay button to selected request
        JButton payButton = new JButton("Pay Now");
        payButton.addActionListener(e -> handlePayment());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(payButton);

        panel.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRunnerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Runner ID", "Name", "Day Available", "Start Time", "End Time", "Rating"};
        runnerTableModel = new DefaultTableModel(columns, 0);
        runnerTable = new JTable(runnerTableModel);
        runnerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        panel.add(new JScrollPane(runnerTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRequestFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createTitledBorder("Submit New Request"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField taskField = new JTextField(20);
        JTextField pickupField = new JTextField(20);
        JTextField deliveryField = new JTextField(20);
        JCheckBox urgentBox = new JCheckBox("Urgent (+RM10)");

        addFormField(panel, gbc, "Task Description:", taskField, 0);
        addFormField(panel, gbc, "Pickup Address:", pickupField, 1);
        addFormField(panel, gbc, "Delivery Address:", deliveryField, 2);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(urgentBox, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitRequest(
                taskField.getText().trim(),
                pickupField.getText().trim(),
                deliveryField.getText().trim(),
                urgentBox.isSelected()
        ));

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitPanel.add(submitButton);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(submitPanel, gbc);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
    }

    private void refreshTables() {
        refreshRequestTable();
        refreshRunnerTable();
    }

    private void refreshRequestTable() {
        requestTableModel.setRowCount(0);
        List<ServiceRequest> requests = serviceController.getRequestsByCustomer(customer.getId());

        for (ServiceRequest req : requests) {
            String assignedRunner = serviceController.getRunnerNameByRequestId(req.getId());
            String paymentStatus = serviceController.getPaymentStatus(req.getId());

            requestTableModel.addRow(new Object[]{
                    req.getId(),
                    req.getTaskDescription(),
                    req.getPickupAddress(),
                    req.getDeliveryAddress(),
                    req.getUrgency(),
                    req.getStatus(),
                    String.format("%.2f", req.getAdditionalCharge()),
                    assignedRunner != null ? assignedRunner : "-",
                    paymentStatus != null ? paymentStatus : "Unpaid"
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

    private void submitRequest(String task, String pickup, String delivery, boolean isUrgent) {
        if (task.isEmpty() || pickup.isEmpty() || delivery.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        // Check runner availability before showing quote
        if (!runnerController.isAnyRunnerAvailableNow()) {
            showError("No runners are available at the moment. Please try again later.");
            return;
        }

        double[] quoteDetails = calculateQuote(pickup, delivery, isUrgent);
        DecimalFormat df = new DecimalFormat("0.00");

        int confirm = JOptionPane.showConfirmDialog(
                this,
                createQuoteMessage(quoteDetails, isUrgent, df),
                "Quote Preview",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ServiceRequest request = isUrgent
                    ? new UrgentServiceRequest(customer.getId(), task, pickup, delivery)
                    : new ServiceRequest(customer.getId(), task, pickup, delivery);

            boolean success = serviceController.submitRequestWithRunnerAssignment(request);
            if (success) {
                int requestId = serviceController.getLatestRequestId(customer.getId());
                initiatePayment(requestId, quoteDetails[4]);
                showSuccess("Request submitted successfully!");
                refreshTables();
            } else {
                showError("Failed to submit request.");
            }
        }
    }

    private void initiatePayment(int requestId, double amount) {
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setPreferredSize(new Dimension(450, 400));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Payment Method Selection
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.add(new JLabel("Payment Method:"));
        JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{
                "Credit Card", "Digital Wallet", "Bank Transfer"
        });
        methodPanel.add(paymentMethodCombo);
        paymentPanel.add(methodPanel);

        // Credit Card Panel
        JPanel cardDetailsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        cardDetailsPanel.setBorder(BorderFactory.createTitledBorder("Credit Card Details"));

        JTextField cardNumberField = new JTextField();
        JTextField expiryField = new JTextField();
        JTextField cvvField = new JTextField();

        cardDetailsPanel.add(new JLabel("Card Number (max 20):"));
        cardDetailsPanel.add(cardNumberField);
        cardDetailsPanel.add(new JLabel("Expiry (MM/YY):"));
        cardDetailsPanel.add(expiryField);
        cardDetailsPanel.add(new JLabel("CVV (3 digits):"));
        cardDetailsPanel.add(cvvField);

        // Digital Wallet Panel
        JPanel walletPanel = new JPanel();
        walletPanel.setLayout(new BoxLayout(walletPanel, BoxLayout.Y_AXIS));
        walletPanel.setBorder(BorderFactory.createTitledBorder("Digital Wallet"));

        JComboBox<String> walletCombo = new JComboBox<>(new String[]{
                "TNG", "GrabPay", "ShopeePay", "Boost"
        });
        JTextField walletPhoneField = new JTextField();
        JTextField walletVerifyCode = new JTextField();

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Select Wallet:"));
        row1.add(walletCombo);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Phone (10 digits):"));
        row2.add(walletPhoneField);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Verification Code (6 digits):"));
        row3.add(walletVerifyCode);

        walletPanel.add(row1);
        walletPanel.add(row2);
        walletPanel.add(row3);

        // Bank Transfer Panel
        JPanel bankPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bankPanel.setBorder(BorderFactory.createTitledBorder("Bank Transfer"));
        JComboBox<String> bankCombo = new JComboBox<>(new String[]{
                "Bank Islam", "Maybank", "CIMB Bank", "Public Bank", "Affin Bank",
                "Hong Leong Bank", "RHB Bank", "Agro Bank", "Alliance Bank",
                "AmBank", "Bank Muamalat", "Bank Simpanan Nasional", "HSBC"
        });
        JTextField accountNumberField = new JTextField();

        bankPanel.add(new JLabel("Select Bank:"));
        bankPanel.add(bankCombo);
        bankPanel.add(new JLabel("Account Number (18–20 digits):"));
        bankPanel.add(accountNumberField);

        // Add all panels
        paymentPanel.add(cardDetailsPanel);
        paymentPanel.add(walletPanel);
        paymentPanel.add(bankPanel);

        // Visibility Handling
        cardDetailsPanel.setVisible(true);
        walletPanel.setVisible(false);
        bankPanel.setVisible(false);

        paymentMethodCombo.addActionListener(e -> {
            String method = (String) paymentMethodCombo.getSelectedItem();
            cardDetailsPanel.setVisible("Credit Card".equals(method));
            walletPanel.setVisible("Digital Wallet".equals(method));
            bankPanel.setVisible("Bank Transfer".equals(method));
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });

        // Dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                paymentPanel,
                "Payment for Request #" + requestId + " - RM" + String.format("%.2f", amount),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String method = (String) paymentMethodCombo.getSelectedItem();
            boolean isValid = true;

            // Validate fields based on selected method
            if ("Credit Card".equals(method)) {
                if (!cardNumberField.getText().matches("\\d{16,20}")) {
                    showError("Invalid card number. Must be 16–20 digits.");
                    isValid = false;
                } else if (!expiryField.getText().matches("\\d{2}/\\d{2}")) {
                    showError("Invalid expiry. Use MM/YY format.");
                    isValid = false;
                } else if (!cvvField.getText().matches("\\d{3}")) {
                    showError("Invalid CVV. Must be 3 digits.");
                    isValid = false;
                }
            } else if ("Digital Wallet".equals(method)) {
                if (!walletPhoneField.getText().matches("\\d{10}")) {
                    showError("Invalid phone number. Must be 10 digits.");
                    isValid = false;
                } else if (!walletVerifyCode.getText().matches("\\d{6}")) {
                    showError("Invalid verification code. Must be 6 digits.");
                    isValid = false;
                }
            } else if ("Bank Transfer".equals(method)) {
                if (!accountNumberField.getText().matches("\\d{18,20}")) {
                    showError("Invalid account number. Must be 18–20 digits.");
                    isValid = false;
                }
            }

            if (!isValid) {
                showError("Please correct the payment details before proceeding.");
                return;
            }

            // ✅ Proceed with payment
            processPayment(requestId, amount, method);
        }
    }


    private void processPayment(int requestId, double amount, String paymentMethod) {
        try {
            // Generate a unique transaction ID
            String transactionId = "TXN" + System.currentTimeMillis();

            // Process payment through controller
            Payment payment = paymentController.processPayment(
                    requestId,
                    amount,
                    paymentMethod,
                    transactionId
            );

            // Confirm the payment
            boolean success = paymentController.confirmPayment(payment.getPaymentId());

            if (success) {
                // Update request status to paid
                serviceController.updateRequestStatus(requestId, "Paid");
                showSuccess("Payment of RM" + String.format("%.2f", amount) +
                        " processed successfully!\nTransaction ID: " + transactionId);
                refreshTables();
            } else {
                showError("Payment confirmation failed.");
            }
        } catch (Exception e) {
            showError("Payment error: " + e.getMessage());
        }
    }

    private void handlePayment() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a request to pay for.");
            return;
        }

        int requestId = (int) requestTableModel.getValueAt(selectedRow, 0);
        double amount = Double.parseDouble(requestTableModel.getValueAt(selectedRow, 6).toString());
        String paymentStatus = requestTableModel.getValueAt(selectedRow, 8).toString();

        if ("Paid".equals(paymentStatus)) {
            showError("This request has already been paid.");
            return;
        }

        initiatePayment(requestId, amount);
    }

    private void trackOrder() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a request to track");
            return;
        }

        int taskId = (int) requestTableModel.getValueAt(selectedRow, 0);
        ServiceRequest request = serviceController.getRequestById(taskId);
        if (request == null) {
            showError("Request not found!");
            return;
        }

        new TrackYourOrder(taskId, this::refreshRequestTable).setVisible(true);
    }

    private String createQuoteMessage(double[] quoteDetails, boolean isUrgent, DecimalFormat df) {
        return "Quote Details:\n" +
                "Distance: " + String.format("%.1f", quoteDetails[0]) + " km\n" +
                "Base Fee: RM" + df.format(quoteDetails[1]) + "\n" +
                "Distance Fee: RM" + df.format(quoteDetails[2]) + "\n" +
                (isUrgent ? "Urgency Surcharge: RM10.00\n" : "") +
                "Tax (10%): RM" + df.format(quoteDetails[3]) + "\n" +
                "--------------------------\n" +
                "Total: RM" + df.format(quoteDetails[4]) + "\n\n" +
                "Do you want to proceed?";
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

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPage("").setVisible(true);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}