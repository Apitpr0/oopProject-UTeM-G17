package model;

public class RunnerStats {
    private int taskCount;
    private double avgRating;

    public RunnerStats(int taskCount, double avgRating) {
        this.taskCount = taskCount;
        this.avgRating = avgRating;
    }

    public int getTaskCount() { return taskCount; }
    public double getAvgRating() { return avgRating; }


}
