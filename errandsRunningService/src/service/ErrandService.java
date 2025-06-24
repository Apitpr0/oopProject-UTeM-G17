package service;

import dao.ErrandDAO;
import dao.RunnerAssignmentDAO;
import dao.RunnerDAO;
import model.Errand;
import model.Runner;

import java.util.List;

public class ErrandService {

    public boolean submitErrand(Errand errand, int customerId) {
        return ErrandDAO.insertErrand(errand, customerId);
    }

    // ✅ Assign any available runner based on current schedule
    public boolean assignRunner(int errandId, String errandTitle, String errandDesc) {
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

    public boolean updateStatus(int errandId, String newStatus) {
        return ErrandDAO.updateErrandStatus(errandId, newStatus);
    }

    public List<Errand> getCustomerErrands(int customerId) {
        return ErrandDAO.getErrandsByCustomer(customerId);
    }

    public Errand getErrandById(int errandId) {
        return ErrandDAO.getErrandById(errandId);
    }
}
