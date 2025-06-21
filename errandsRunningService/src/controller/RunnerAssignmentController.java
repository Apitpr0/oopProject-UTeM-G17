package controller;

import dao.RunnerAssignmentDAO;
import model.RunnerAssignment;

import java.util.List;

public class RunnerAssignmentController {

    public List<RunnerAssignment> getAssignmentsForRunner(int runnerId) {
        return RunnerAssignmentDAO.getAssignmentsByRunner(runnerId);
    }

    // NEW: Update status method
    public boolean updateAssignmentStatus(int assignmentId, String newStatus) {
        return RunnerAssignmentDAO.updateStatus(assignmentId, newStatus);
    }
}
