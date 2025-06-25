package view;
import controller.ServiceController;
import model.*;
import java.util.List;
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


    public AdminDashboard(Admin admin) {
        this.admin = admin;
        serviceController = new ServiceController();


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
            loadCompletedErrands();
            loadRunnerPerformance();
            JOptionPane.showMessageDialog(this, "Data refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exportButton = new JButton("📊 Export Performance");
        exportButton.addActionListener(e -> exportPerformanceData());


        JButton logoutButton = new JButton("🚪 Log Out");
        logoutButton.setPreferredSize(new Dimension(120, 30));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // close current window
                SwingUtilities.invokeLater(() -> new LoginPage("")); // reopen login
            }
        });

        controlPanel.add(refreshButton);
        controlPanel.add(exportButton);
        controlPanel.add(logoutButton);

        add(tabbedPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        JPanel manageUserPanel = createUserManagementPanel();
        tabbedPane.addTab("👥 Manage Users", manageUserPanel);

        // Initial load
        createUserManagementPanel();
        addSearchBarToCompletedPanel(completedPanel);
        addSearchBarToPerformancePanel(performancePanel);
        loadCompletedErrands();
        loadRunnerPerformance();

        setVisible(true);
    }
    private JPanel createUserManagementPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Name", "Email", "Role"};
        DefaultTableModel userModel = new DefaultTableModel(cols, 0);
        JTable userTable = new JTable(userModel);

        // Load user data
        List<User> users = serviceController.getAllUsers(); // ← You need to create this in your DAO
        for (User u : users) {
            userModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");

        // Add button logic (simplified, can expand to forms)


        editButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                int userId = (int) userModel.getValueAt(row, 0);
                String currentName = (String) userModel.getValueAt(row, 1);
                String currentEmail = (String) userModel.getValueAt(row, 2);
                String currentRole = (String) userModel.getValueAt(row, 3);

                JTextField nameField = new JTextField(currentName);
                JTextField emailField = new JTextField(currentEmail);
                String[] roles = {"admin", "customer", "runner"};
                JComboBox<String> roleBox = new JComboBox<>(roles);
                roleBox.setSelectedItem(currentRole);

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Email:"));
                panel.add(emailField);
                panel.add(new JLabel("Role:"));
                panel.add(roleBox);

                int result = JOptionPane.showConfirmDialog(null, panel, "✏️ Edit User",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String newName = nameField.getText().trim();
                    String newEmail = emailField.getText().trim();
                    String newRole = (String) roleBox.getSelectedItem();

                    if (!newName.isEmpty() && !newEmail.isEmpty()) {
                        User updatedUser = new User();
                        updatedUser.setId(userId);
                        updatedUser.setName(newName);
                        updatedUser.setEmail(newEmail);
                        updatedUser.setRole(newRole);

                        boolean success = serviceController.updateUser(updatedUser);
                        if (success) {
                            // Update UI table
                            userModel.setValueAt(newName, row, 1);
                            userModel.setValueAt(newEmail, row, 2);
                            userModel.setValueAt(newRole, row, 3);
                            JOptionPane.showMessageDialog(null, "✅ User updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "❌ Failed to update user.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Name and Email cannot be empty!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a user to edit.");
            }
        });

        deleteButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row >= 0) {
                int userId = (int) userModel.getValueAt(row, 0);
                serviceController.deleteUser(userId); // ← You implement this
                userModel.removeRow(row);
            }
        });
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        userPanel.add(buttonPanel, BorderLayout.SOUTH);
        return userPanel;
    }

    private void exportPerformanceData() {
        StringBuilder sb = new StringBuilder();

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

        performancePanel.add(new JScrollPane(performanceTable), BorderLayout.CENTER);

        return performancePanel;
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
                    String.format("RM%.2f", req.getAdditionalCharge()),
                    runnerName != null ? runnerName : "-"
            });
        }
    }

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
    private void addSearchBarToCompletedPanel(JPanel panel) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("🔍 Search ID");

        searchButton.addActionListener(e -> {
            String input = searchField.getText().trim();
            if (input.isEmpty()) return;

            for (int i = 0; i < completedTableModel.getRowCount(); i++) {
                int id = Integer.parseInt(completedTableModel.getValueAt(i, 0).toString());
                if (Integer.toString(id).equals(input)) {
                    completedTable.setRowSelectionInterval(i, i);
                    completedTable.scrollRectToVisible(completedTable.getCellRect(i, 0, true));
                    break;
                }
            }
        });

        searchPanel.add(new JLabel("Search Request ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
    }

    private void addSearchBarToPerformancePanel(JPanel panel) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("🔍 Search ID");

        searchButton.addActionListener(e -> {
            String input = searchField.getText().trim();
            if (input.isEmpty()) return;

            for (int i = 0; i < performanceTableModel.getRowCount(); i++) {
                int id = Integer.parseInt(performanceTableModel.getValueAt(i, 1).toString());
                if (Integer.toString(id).equals(input)) {
                    performanceTable.setRowSelectionInterval(i, i);
                    performanceTable.scrollRectToVisible(performanceTable.getCellRect(i, 0, true));
                    break;
                }
            }
        });

        searchPanel.add(new JLabel("Search Runner ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
    }

}