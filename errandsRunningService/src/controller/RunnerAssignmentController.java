package controller;

import dao.RunnerAssignmentDAO;
import model.RunnerAssignment;

import java.util.List;

public class RunnerAssignmentController {

    public List<RunnerAssignment> getAssignmentsForRunner(int runnerId) {
        return RunnerAssignmentDAO.getAssignmentsByRunner(runnerId);
    }
}
