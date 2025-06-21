package controller;

import model.Errand;
import service.ErrandService;

import java.util.List;

public class ErrandController {
    private ErrandService errandService = new ErrandService();

    public boolean submitErrand(String type, String description, String pickup, String dropoff, int customerId) {
        Errand errand = new Errand(type, description, pickup, dropoff);
        return errandService.submitErrand(errand, customerId);
    }

    public boolean assignRunnerToErrand(int errandId, int runnerId) {
        return errandService.assignRunner(errandId, runnerId);
    }

    public boolean updateErrandStatus(int errandId, String status) {
        return errandService.updateStatus(errandId, status);
    }

    public List<Errand> getErrandsForCustomer(int customerId) {
        return errandService.getCustomerErrands(customerId);
    }

    public Errand getErrandDetails(int errandId) {
        return errandService.getErrandById(errandId);
    }
}
