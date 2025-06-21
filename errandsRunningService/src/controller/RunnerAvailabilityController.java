    package controller;

    import dao.RunnerAvailabilityDAO;
    import model.RunnerAvailability;

    import java.util.List;

    public class RunnerAvailabilityController {

        // Return a message: null = success, non-null = error message
        public String addAvailability(RunnerAvailability availability) {
            if (RunnerAvailabilityDAO.hasOverlap(availability)) {
                return "❌ Overlapping availability exists for the selected day and time.";
            }

            boolean success = RunnerAvailabilityDAO.addAvailability(availability);
            return success ? null : "❌ Failed to add availability. Please try again.";
        }

        public List<RunnerAvailability> getAvailabilityByRunner(int runnerId) {
            return RunnerAvailabilityDAO.getAvailabilityByRunner(runnerId);
        }

        public boolean deleteAvailability(int id) {
            return RunnerAvailabilityDAO.deleteAvailability(id);
        }
    }
