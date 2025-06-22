package service;

import dao.ErrandDAO;
import dao.RunnerAssignmentDAO;
import dao.RunnerDAO;
import model.Errand;
import java.util.List;

public class ErrandService {

    public boolean submitErrand(Errand errand, int customerId) {
        return ErrandDAO.insertErrand(errand, customerId);
    }

    public boolean assignRunner(int errandId, String errandTitle, String errandDesc) {
        if (!RunnerDAO.isBobAvailable()) {
            System.out.println("❌ Auto-assignment failed: Bob is not available");
            return false;
        }

        return RunnerAssignmentDAO.assignBobToErrandIfAvailable(errandId, errandTitle, errandDesc);
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