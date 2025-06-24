package controller;

import dao.RunnerAssignmentDAO;
import dao.RunnerDAO;
import model.Runner;
import model.RunnerAssignment;

import java.util.List;

public class RunnerAssignmentController {

    // Get assignments for a specific runner
    public List<RunnerAssignment> getAssignmentsForRunner(int runnerId) {
        return RunnerAssignmentDAO.getAssignmentsByRunner(runnerId);
    }

    // Update status of an assignment (e.g., In Progress, Completed)
    public boolean updateAssignmentStatus(int assignmentId, String newStatus) {
        return RunnerAssignmentDAO.updateStatus(assignmentId, newStatus);
    }

    // ✅ New general-purpose auto-assignment based on availability schedule
    public boolean autoAssignToAnyAvailableRunner(int errandId, String title, String description) {
        List<Runner> availableRunners = RunnerDAO.getAvailableRunnersNow();

        for (Runner runner : availableRunners) {
            boolean assigned = RunnerAssignmentDAO.assignRunnerToErrand(runner.getId(), errandId);

            if (assigned) {
                System.out.println("✅ Auto-assigned runner_id=" + runner.getId() + " to errand_id=" + errandId);
                return true;
            }
        }

        System.out.println("❌ No available runners to assign at this time.");
        return false;
    }
}
