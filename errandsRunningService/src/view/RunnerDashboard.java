package view;

import controller.RunnerAvailabilityController;
import controller.RunnerAssignmentController;
import model.RunnerAvailability;
import model.RunnerAssignment;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class RunnerDashboard extends JFrame {
    private User user;

    // UI components
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

        // Header panel with welcome label and logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel("Welcome, " + user.getName() + " (Runner)!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage(""));
        });

        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabs
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

        // Add button logic with overlap check
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

        // Delete selected availability
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
        panel.add(new JScrollPane(assignmentTable), BorderLayout.CENTER);

        loadAssignments();
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JTextField nameField = new JTextField(user.getName());
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        JButton saveButton = new JButton("Update Name");
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
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(new JLabel());  // empty placeholder
        panel.add(saveButton);
        return panel;
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
