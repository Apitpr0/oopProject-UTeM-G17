package view;

import controller.ServiceController;
import controller.RunnerController;
import model.Runner;
import model.Admin;
import model.RunnerStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private Admin admin;
    private JTabbedPane tabbedPane;
    private JTable completedTable;
    private JTable performanceTable;
    private DefaultTableModel completedTableModel;
    private DefaultTableModel performanceTableModel;
    private ServiceController serviceController;
    private RunnerController runnerController;

    // Summary labels for performance statistics
    private JLabel totalTasksLabel;
    private JLabel avgRatingLabel;
    private JLabel topRunnerLabel;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        serviceController = new ServiceController();
        runnerController = new RunnerController();

        setTitle("Admin Dashboard - Welcome " + admin.getName());
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Tab 1: Completed Errands
        JPanel completedPanel = new JPanel(new BorderLayout());
        String[] completedCols = {"Request ID", "Customer ID", "Task", "Pickup", "Delivery", "Charge", "Runner"};
        completedTableModel = new DefaultTableModel(completedCols, 0);
        completedTable = new JTable(completedTableModel);
        completedTable.setAutoCreateRowSorter(true);
        completedTable.setRowHeight(25);
        completedPanel.add(new JScrollPane(completedTable), BorderLayout.CENTER);
        tabbedPane.addTab("✅ Completed Errands", completedPanel);

        // Tab 2: Enhanced Runner Performance
        JPanel performancePanel = createPerformancePanel();
        tabbedPane.addTab("📈 Runner Performance", performancePanel);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton refreshButton = new JButton("🔄 Refresh All");
        refreshButton.addActionListener(e -> {
//            loadCompletedErrands();
            loadRunnerPerformance();
            JOptionPane.showMessageDialog(this, "Data refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exportButton = new JButton("📊 Export Performance");
        exportButton.addActionListener(e -> exportPerformanceData());

        controlPanel.add(refreshButton);
        controlPanel.add(exportButton);

        add(tabbedPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Initial load
//        loadCompletedErrands();
        loadRunnerPerformance();

        setVisible(true);
    }
    private void exportPerformanceData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Runner Performance Report\n");
        sb.append("=========================\n\n");
        sb.append("Total Completed Tasks: ").append(totalTasksLabel.getText()).append("\n");
        sb.append("Overall Average Rating: ").append(avgRatingLabel.getText()).append("\n");
        sb.append("Top Performer: ").append(topRunnerLabel.getText()).append("\n\n");

        sb.append("Detailed Performance:\n");
        sb.append("Rank\tRunner ID\tRunner Name\tCompleted Tasks\tAvg Rating\tPerformance Score\n");

        for (int i = 0; i < performanceTableModel.getRowCount(); i++) {
            for (int j = 0; j < performanceTableModel.getColumnCount(); j++) {
                sb.append(performanceTableModel.getValueAt(i, j)).append("\t");
            }
            sb.append("\n");
        }

        // Show export dialog
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Performance Report", JOptionPane.INFORMATION_MESSAGE);
    }
    private JPanel createPerformancePanel() {
        JPanel performancePanel = new JPanel(new BorderLayout());

        // Enhanced table with more columns
        String[] perfCols = {"Rank", "Runner ID", "Runner Name", "Completed Tasks", "Avg Rating", "Performance Score"};
        performanceTableModel = new DefaultTableModel(perfCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        performanceTable = new JTable(performanceTableModel);
        performanceTable.setAutoCreateRowSorter(true);
        performanceTable.setRowHeight(30);
        performanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        performanceTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        performanceTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Runner ID
        performanceTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Runner Name
        performanceTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Completed Tasks
        performanceTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Avg Rating
        performanceTable.getColumnModel().getColumn(5).setPreferredWidth(130); // Performance Score

        // Summary panel at the top
        JPanel summaryPanel = createSummaryPanel();

        // Add components to performance panel
        performancePanel.add(summaryPanel, BorderLayout.NORTH);
        performancePanel.add(new JScrollPane(performanceTable), BorderLayout.CENTER);

        return performancePanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Performance Summary"));
        summaryPanel.setBackground(new Color(240, 248, 255));

        // First row of statistics
        JPanel statsRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsRow1.setOpaque(false);

        totalTasksLabel = new JLabel("0");
        totalTasksLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalTasksLabel.setForeground(new Color(0, 100, 0));

        avgRatingLabel = new JLabel("0.00");
        avgRatingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        avgRatingLabel.setForeground(new Color(0, 0, 150));

        statsRow1.add(new JLabel("Total Completed Tasks: "));
        statsRow1.add(totalTasksLabel);
        statsRow1.add(Box.createHorizontalStrut(30));
        statsRow1.add(new JLabel("Overall Average Rating: "));
        statsRow1.add(avgRatingLabel);
        statsRow1.add(new JLabel("⭐"));

        // Second row - top performer
        JPanel statsRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsRow2.setOpaque(false);

        topRunnerLabel = new JLabel("Loading...");
        topRunnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topRunnerLabel.setForeground(new Color(150, 0, 0));

        statsRow2.add(new JLabel("🏆 Top Performer: "));
        statsRow2.add(topRunnerLabel);

        summaryPanel.add(statsRow1);
        summaryPanel.add(statsRow2);

        return summaryPanel;
    }

////    private void loadCompletedErrands() {
//        completedTableModel.setRowCount(0);
//        List<ServiceRequest> completed = serviceController.getCompletedRequests();
//
//        for (ServiceRequest req : completed) {
//            String runnerName = serviceController.getRunnerNameByRequestId(req.getId());
//            completedTableModel.addRow(new Object[]{
//                    req.getId(),
//                    req.getCustomerId(),
//                    req.getTaskDescription(),
//                    req.getPickupAddress(),
//                    req.getDeliveryAddress(),
//                    String.format("$%.2f", req.getAdditionalCharge()),
//                    runnerName != null ? runnerName : "-"
//            });
//        }
//    }

    private void loadRunnerPerformance() {
        performanceTableModel.setRowCount(0);
        Map<Runner, RunnerStats> runnerPerformance = serviceController.getRunnerPerformanceWithRatings();

        int totalTasks = 0;
        double totalRatingSum = 0;
        int totalRatedTasks = 0;
        String topRunnerName = "None";
        double topPerformanceScore = 0;
        int rank = 1;

        for (Map.Entry<Runner, RunnerStats> entry : runnerPerformance.entrySet()) {
            Runner r = entry.getKey();
            RunnerStats stats = entry.getValue();

            // Calculate performance score (70% tasks + 30% rating quality)
            double performanceScore = (stats.getTaskCount() * 0.7) + (stats.getAvgRating() * 3.0);

            // Track top performer
            if (performanceScore > topPerformanceScore) {
                topPerformanceScore = performanceScore;
                topRunnerName = r.getName() + " (Score: " + String.format("%.1f", performanceScore) + ")";
            }

            // Add row with ranking
            performanceTableModel.addRow(new Object[]{
                    rank++,
                    r.getId(),
                    r.getName(),
                    stats.getTaskCount(),
                    stats.getAvgRating() > 0 ? String.format("%.2f ⭐", stats.getAvgRating()) : "Not rated",
                    String.format("%.1f", performanceScore)
            });

            // Calculate totals for summary
            totalTasks += stats.getTaskCount();
            if (stats.getAvgRating() > 0) {
                totalRatingSum += stats.getAvgRating();
                totalRatedTasks++;
            }
        }
    }
}