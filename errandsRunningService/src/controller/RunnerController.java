package controller;

import dao.RunnerDAO;
import model.Runner;

import java.util.List;

public class RunnerController {

    public List<Runner> getAvailableRunners() {
        return RunnerDAO.getAvailableRunners();
    }

    public boolean updateAvailability(int runnerId, boolean available) {
        return RunnerDAO.setRunnerAvailability(runnerId, available);
    }
}
