package controller;

import dao.RunnerDAO;
import model.Runner;

import java.util.List;

public class RunnerController {

    public List<Runner> getAvailableRunnersNow() {
        return RunnerDAO.getAvailableRunnersNow();
    }

    public boolean updateAvailability(int runnerId, boolean available) {
        return RunnerDAO.setRunnerAvailability(runnerId, available);
    }

    public List<Runner> getAllRunnersWithAvailability() {
        return RunnerDAO.getAllRunnersWithAvailability();
    }

}
