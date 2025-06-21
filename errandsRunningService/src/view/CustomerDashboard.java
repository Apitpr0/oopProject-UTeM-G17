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
        String[] runnerColumns = {"Runner ID", "Name", "Day Available", "Start Time", "End Time"};
        runnerTableModel = new DefaultTableModel(runnerColumns, 0);
        runnerTable = new JTable(runnerTableModel);
        centerPanel.add(new JScrollPane(runnerTable));

        add(centerPanel, BorderLayout.CENTER);

        // Form inputs
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        JTextField taskField = new JTextField();
        JTextField pickupField = new JTextField();
        JTextField deliveryField = new JTextField();
        JCheckBox urgentBox = new JCheckBox("Urgent (+RM10)");

        formPanel.add(new JLabel("Task Description:"));
        formPanel.add(taskField);
        formPanel.add(new JLabel("Pickup Address:"));
        formPanel.add(pickupField);
        formPanel.add(new JLabel("Delivery Address:"));
        formPanel.add(deliveryField);
        formPanel.add(new JLabel(""));
        formPanel.add(urgentBox);

        JButton submitButton = new JButton("Submit Request");
        formPanel.add(submitButton);

        add(formPanel, BorderLayout.SOUTH);

        // Button logic
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
        List<Runner> availableRunners = runnerController.getAvailableRunners();

        for (Runner runner : availableRunners) {
            String availability = runner.getAvailability(); // e.g. "Monday 08:00-17:00"
            String day = "-", start = "-", end = "-";

            if (availability != null && availability.contains(" ")) {
                String[] parts = availability.split(" ");
                if (parts.length >= 2) {
                    day = parts[0];
                    String[] timeParts = parts[1].split("-");
                    if (timeParts.length == 2) {
                        start = timeParts[0];
                        end = timeParts[1];
                    }
                }
            }

            runnerTableModel.addRow(new Object[]{
                    runner.getId(),
                    runner.getName(),
                    day,
                    start,
                    end
            });
        }
    }

}