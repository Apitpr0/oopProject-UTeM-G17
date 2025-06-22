package view;

import controller.ServiceController;
import controller.RunnerController;
import model.ServiceRequest;
import model.Runner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable completedTable;
    private JTable performanceTable;
    private DefaultTableModel completedTableModel;
    private DefaultTableModel performanceTableModel;
    private ServiceController serviceController;
    private RunnerController runnerController;

    public AdminDashboard() {
        serviceController = new ServiceController();
        runnerController = new RunnerController();

        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Tab 1: Completed Errands
        JPanel completedPanel = new JPanel(new BorderLayout());
        String[] completedCols = {"Request ID", "Customer ID", "Task", "Pickup", "Delivery", "Charge", "Runner"};
        completedTableModel = new DefaultTableModel(completedCols, 0);
        completedTable = new JTable(completedTableModel);
        completedPanel.add(new JScrollPane(completedTable), BorderLayout.CENTER);
        tabbedPane.addTab("✅ Completed Errands", completedPanel);

        // Tab 2: Runner Performance
        JPanel performancePanel = new JPanel(new BorderLayout());
        String[] perfCols = {"Runner ID", "Runner Name", "Completed Tasks"};
        performanceTableModel = new DefaultTableModel(perfCols, 0);
        performanceTable = new JTable(performanceTableModel);
        performancePanel.add(new JScrollPane(performanceTable), BorderLayout.CENTER);
        tabbedPane.addTab("📈 Runner Performance", performancePanel);

        // Refresh Button
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> {
            loadCompletedErrands();
            loadRunnerPerformance();
        });

        add(tabbedPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        // Initial load
        loadCompletedErrands();
        loadRunnerPerformance();

        setVisible(true);
    }

    private void loadCompletedErrands() {
        completedTableModel.setRowCount(0);
        List<ServiceRequest> completed = serviceController.getCompletedRequests();

        for (ServiceRequest req : completed) {
            String runnerName = serviceController.getRunnerNameByRequestId(req.getId());
            completedTableModel.addRow(new Object[]{
                    req.getId(),
                    req.getCustomerId(),
                    req.getTaskDescription(),
                    req.getPickupAddress(),
                    req.getDeliveryAddress(),
                    String.format("%.2f", req.getAdditionalCharge()),
                    runnerName != null ? runnerName : "-"
            });
        }
    }

    private void loadRunnerPerformance() {
        performanceTableModel.setRowCount(0);
        Map<Runner, Integer> runnerPerformance = serviceController.getRunnerPerformance();

        for (Map.Entry<Runner, Integer> entry : runnerPerformance.entrySet()) {
            Runner r = entry.getKey();
            int taskCount = entry.getValue();
            performanceTableModel.addRow(new Object[]{
                    r.getId(),
                    r.getName(),
                    taskCount
            });
        }
    }
}
