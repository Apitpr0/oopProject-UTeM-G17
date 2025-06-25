package view;

import controller.Cust_History;
import model.ServiceRequest;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class cust_history extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private Cust_History historyController;
    private Customer customer;

    public cust_history(Customer customer) {
        this.customer = customer;
        this.historyController = new Cust_History();
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new String[]{
                "Request ID", "Task", "Pickup", "Delivery", "Runner", "Status", "Rating"
        }, 0);
        historyTable = new JTable(tableModel);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // Rate Button
        JButton rateBtn = new JButton("Rate Runner");
        rateBtn.addActionListener(e -> rateSelectedRunner());
        add(rateBtn, BorderLayout.SOUTH);

        loadCompletedRequests();
    }

    private void loadCompletedRequests() {
        tableModel.setRowCount(0);
        List<ServiceRequest> list = historyController.getCompletedRequestsByCustomer(customer.getId());
        for (ServiceRequest req : list) {
            tableModel.addRow(new Object[]{
                    req.getId(),
                    req.getTaskDescription(),
                    req.getPickupAddress(),
                    req.getDeliveryAddress(),
                    req.getRunnerName(),
                    req.getStatus(),
                    req.getRating()
            });
        }
    }

    private void rateSelectedRunner() {
        int selected = historyTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row.");
            return;
        }

        int requestId = (int) tableModel.getValueAt(selected, 0);
        String runnerName = (String) tableModel.getValueAt(selected, 4);
        int runnerId = ((List<ServiceRequest>) historyController.getCompletedRequestsByCustomer(customer.getId()))
                .get(selected).getRunnerId(); // Simple trick

        String ratingInput = JOptionPane.showInputDialog(this,
                "Rate runner \"" + runnerName + "\" (1–5):");

        if (ratingInput == null) return; // Cancel

        try {
            int rating = Integer.parseInt(ratingInput.trim());
            if (rating < 1 || rating > 5) throw new NumberFormatException();

            String comment = JOptionPane.showInputDialog(this, "Optional comment:");
            if (comment == null) comment = "";

            boolean success = historyController.submitRating(requestId, customer.getId(), runnerId, rating, comment);
            if (success) {
                JOptionPane.showMessageDialog(this, "Rated successfully!");
                loadCompletedRequests(); // Refresh
            } else {
                JOptionPane.showMessageDialog(this, "Rating failed. You might’ve already rated this.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid rating. Please enter a number from 1 to 5.");
        }
    }
}
