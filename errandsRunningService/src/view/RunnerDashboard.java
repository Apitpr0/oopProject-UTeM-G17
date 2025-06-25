package view;

import controller.RunnerAvailabilityController;
import controller.RunnerAssignmentController;
import model.RunnerAvailability;
import model.RunnerAssignment;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class RunnerDashboard extends JFrame {
    private User user;

    private JLabel welcomeLabel;
    private JTable availabilityTable;
    private DefaultTableModel availabilityTableModel;
    private JComboBox<String> dayComboBox;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTable assignmentTable;
    private DefaultTableModel assignmentTableModel;

    private RunnerAvailabilityController availabilityController = new RunnerAvailabilityController();
    private RunnerAssignmentController assignmentController = new RunnerAssignmentController();

    public RunnerDashboard(User user) {
        this.user = user;

        setTitle("Runner Dashboard - Welcome " + user.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel("Welcome, " + user.getName() + " (Runner)!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginPage(""));
            }
        });

        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Availability", createAvailabilityPanel());
        tabbedPane.addTab("Current Assignments", createAssignmentPanel());
        tabbedPane.addTab("Profile", createProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        availabilityTableModel = new DefaultTableModel(new String[]{"ID", "Day", "Start Time", "End Time"}, 0);
        availabilityTable = new JTable(availabilityTableModel);
        panel.add(new JScrollPane(availabilityTable), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 5));
        dayComboBox = new JComboBox<>(new String[]{
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        });
        startTimeField = new JTextField("08:00");
        endTimeField = new JTextField("17:00");
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        inputPanel.add(new JLabel("Day:"));
        inputPanel.add(dayComboBox);
        inputPanel.add(new JLabel("Start:"));
        inputPanel.add(startTimeField);
        inputPanel.add(new JLabel("End:"));
        inputPanel.add(endTimeField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        panel.add(inputPanel, BorderLayout.SOUTH);

        loadAvailability();

        addButton.addActionListener(e -> {
            String day = dayComboBox.getSelectedItem().toString();
            String start = startTimeField.getText().trim();
            String end = endTimeField.getText().trim();

            try {
                Time startTime = Time.valueOf(start + ":00");
                Time endTime = Time.valueOf(end + ":00");

                RunnerAvailability availability = new RunnerAvailability(
                        0, user.getId(), day, startTime, endTime
                );

                String result = availabilityController.addAvailability(availability);
                if (result == null) {
                    JOptionPane.showMessageDialog(this, "Availability added.");
                    loadAvailability();
                } else {
                    JOptionPane.showMessageDialog(this, result, "Warning", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid time format. Use HH:MM (24-hour format)",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = availabilityTable.getSelectedRow();
            if (selectedRow >= 0) {
                int availabilityId = (int) availabilityTableModel.getValueAt(selectedRow, 0);
                boolean success = availabilityController.deleteAvailability(availabilityId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Availability deleted.");
                    loadAvailability();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete availability.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        return panel;
    }

    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        assignmentTableModel = new DefaultTableModel(new String[]{
                "ID", "Title", "Description", "Status"
        }, 0);
        assignmentTable = new JTable(assignmentTableModel);

        // 🌈 Set custom renderer for status column with bold + color
        assignmentTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                c.setForeground(Color.BLACK);
                setOpaque(true);

                if (!isSelected) {
                    switch (status.toLowerCase()) {
                        case "pending":
                            c.setBackground(Color.YELLOW); // Same as ⏳ Pending
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                            value = "⏳ Pending";
                            break;

                        case "in progress":
                            c.setBackground(new Color(173, 216, 230)); // Same as 🔄 In Progress
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                            value = "🔄 In Progress";
                            break;

                        case "completed":
                            c.setBackground(Color.GREEN); // Same as ✅ Completed
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                            value = "✅ Completed";
                            break;

                        case "assigned":
                            c.setBackground(new Color(255, 255, 204)); // Optional: Pale yellow
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                            value = "📌 Assigned";
                            break;

                        default:
                            c.setBackground(Color.WHITE);
                            value = status; // fallback
                    }

                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        });

        panel.add(new JScrollPane(assignmentTable), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        JLabel statusLabel = new JLabel("Update Status:");
        String[] statuses = {"Pending", "In Progress", "Completed"};
        JComboBox<String> statusDropdown = new JComboBox<>(statuses);
        JButton updateStatusButton = new JButton("Update");
        JButton refreshButton = new JButton("Refresh");

        statusPanel.add(statusLabel);
        statusPanel.add(statusDropdown);
        statusPanel.add(updateStatusButton);
        statusPanel.add(refreshButton);

        panel.add(statusPanel, BorderLayout.SOUTH);

        updateStatusButton.addActionListener(e -> {
            int selectedRow = assignmentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int assignmentId = (int) assignmentTableModel.getValueAt(selectedRow, 0);
                String newStatus = statusDropdown.getSelectedItem().toString();

                boolean success = assignmentController.updateAssignmentStatus(assignmentId, newStatus);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Status updated.");
                    loadAssignments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update status.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an assignment to update.");
            }
        });

        refreshButton.addActionListener(e -> loadAssignments());

        loadAssignments();
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        JLabel emailLabel = new JLabel("Email:");

        JTextField nameField = new JTextField(user.getName());
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);

        Dimension fieldSize = new Dimension(200, 25);  // Smaller height
        nameField.setPreferredSize(fieldSize);
        emailField.setPreferredSize(fieldSize);

        nameField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton saveButton = new JButton("Update");
        saveButton.setFocusPainted(false);
        saveButton.setBackground(new Color(66, 133, 244));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                boolean updated = dao.UserDAO.updateName(user.getId(), newName);
                if (updated) {
                    user.setName(newName);
                    JOptionPane.showMessageDialog(this, "Name updated successfully.");
                    welcomeLabel.setText("Welcome, " + user.getName() + " (Runner)!");
                    setTitle("Runner Dashboard - Welcome " + user.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update name.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        outerPanel.add(panel, BorderLayout.CENTER);
        return outerPanel;
    }


    private void loadAvailability() {
        availabilityTableModel.setRowCount(0);
        List<RunnerAvailability> list = availabilityController.getAvailabilityByRunner(user.getId());
        for (RunnerAvailability a : list) {
            availabilityTableModel.addRow(new Object[]{
                    a.getId(),
                    a.getDayOfWeek(),
                    a.getStartTime().toString().substring(0, 5),
                    a.getEndTime().toString().substring(0, 5)
            });
        }
    }

    private void loadAssignments() {
        assignmentTableModel.setRowCount(0);
        List<RunnerAssignment> list = assignmentController.getAssignmentsForRunner(user.getId());
        for (RunnerAssignment a : list) {
            assignmentTableModel.addRow(new Object[]{
                    a.getId(),
                    a.getTitle(),
                    a.getDescription(),
                    a.getStatus()
            });
        }
    }
}
