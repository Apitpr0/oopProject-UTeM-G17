package service;

import dao.ErrandDAO;
import model.Errand;
import java.util.List;

import dao.RunnerAssignmentDAO;
import dao.RunnerDAO;

public class ErrandService {

    public boolean submitErrand(Errand errand, int customerId) {
        return ErrandDAO.insertErrand(errand, customerId);
    }

    public boolean assignRunner(int errandId, String errandTitle, String errandDesc) {
        Integer bobId = RunnerDAO.getBobIfAvailableNow();
        if (bobId != null && bobId == 2) {
            return RunnerAssignmentDAO.assignBobToErrandIfAvailable(errandId, errandTitle, errandDesc);
        } else {
            System.out.println("❌ Auto-assignment failed: Bob is not available.");
            return false;
        }
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
