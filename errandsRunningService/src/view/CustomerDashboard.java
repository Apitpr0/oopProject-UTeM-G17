package view;

import controller.ServiceController;
import model.Customer;
import model.ServiceRequest;
import model.UrgentServiceRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private Customer customer;
    private ServiceController controller;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    public CustomerDashboard(Customer customer) {
        this.customer = customer;
        this.controller = new ServiceController();

        setTitle("Customer Dashboard - Welcome " + customer.getName());
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getName(), JLabel.LEFT);
        JButton submitButton = new JButton("Submit New Request");
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(submitButton, BorderLayout.EAST);

        // Table
        String[] columnNames = {
                "Request ID", "Task", "Pickup Address", "Delivery Address",
                "Urgency", "Extra Charge (RM)", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        requestTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestTable);

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button Event
        submitButton.addActionListener(e -> submitNewRequest());

        // Load data
        refreshTable();

        setVisible(true);
    }

    private void submitNewRequest() {
        String task = JOptionPane.showInputDialog(this, "Enter Task Description:");
        if (task == null || task.trim().isEmpty()) return;

        String pickup = JOptionPane.showInputDialog(this, "Enter Pickup Address:");
        if (pickup == null || pickup.trim().isEmpty()) return;

        String delivery = JOptionPane.showInputDialog(this, "Enter Delivery Address:");
        if (delivery == null || delivery.trim().isEmpty()) return;

        int urgent = JOptionPane.showConfirmDialog(this,
                "Is this request urgent? (Extra RM10 will be charged)",
                "Urgent Request", JOptionPane.YES_NO_OPTION);

        boolean isUrgent = urgent == JOptionPane.YES_OPTION;
        boolean success;

        // ✅ Place your constructor call HERE:
        if (isUrgent) {
            UrgentServiceRequest urgentReq = new UrgentServiceRequest(
                    customer.getId(), task, pickup, delivery
            );
            success = controller.submitUrgentRequest(urgentReq);
        } else {
            ServiceRequest req = new ServiceRequest(
                    customer.getId(), task, pickup, delivery
            );
            success = controller.submitRequest(req);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Request submitted!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to submit request.");
        }
    }

    private void refreshTable() {
        List<ServiceRequest> requests = controller.getCustomerRequests(customer.getId());
        tableModel.setRowCount(0); // Clear table

        for (ServiceRequest req : requests) {
            tableModel.addRow(new Object[]{
                    req.getId(),
                    req.getTaskDescription(),
                    req.getPickupAddress(),
                    req.getDeliveryAddress(),
                    req.getUrgency(),
                    String.format("%.2f", req.getAdditionalCharge()),
                    req.getStatus()
            });
        }
    }
}
