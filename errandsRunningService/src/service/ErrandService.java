package service;

import dao.ErrandDAO;
import model.Errand;
import java.util.List;

public class ErrandService {

    public boolean submitErrand(Errand errand, int customerId) {
        return ErrandDAO.insertErrand(errand, customerId);
    }

    public boolean assignRunner(int errandId, int runnerId) {
        return ErrandDAO.updateRunnerAssignment(errandId, runnerId);
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
