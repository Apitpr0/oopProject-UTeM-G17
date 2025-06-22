package controller;

import dao.RunnerAssignmentDAO;
import dao.RunnerDAO;
import model.RunnerAssignment;
import java.util.List;

public class RunnerAssignmentController {

    public List<RunnerAssignment> getAssignmentsForRunner(int runnerId) {
        return RunnerAssignmentDAO.getAssignmentsByRunner(runnerId);
    }

    public boolean updateAssignmentStatus(int assignmentId, String newStatus) {
        return RunnerAssignmentDAO.updateStatus(assignmentId, newStatus);
    }

    // Auto-assign only to Bob if available
    public boolean autoAssignToBobOnly(int errandId, String title, String description) {
        if (!RunnerDAO.isBobAvailable()) {
            System.out.println("❌ Bob is not available now. No runner will be assigned.");
            return false;
        }

        return RunnerAssignmentDAO.assignBobToErrandIfAvailable(errandId, title, description);
    }
}