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

    // ✅ Auto-assign Bob (runner_id = 2) if he is available
    public boolean autoAssignToBobOnly(int errandId, String title, String description) {
        Integer bobId = RunnerDAO.getBobIfAvailableNow();

        if (bobId != null && bobId == 2) {
            return RunnerAssignmentDAO.assignBobToErrandIfAvailable(errandId, title, description);
        } else {
            System.out.println("❌ Bob is not available now. No runner will be assigned.");
            return false;
        }
    }
}
