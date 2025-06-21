package model;

public class RunnerAssignment {
    private int id;
    private int runnerId;
    private String title;
    private String description;
    private String status;

    public RunnerAssignment(int id, int runnerId, String title, String description, String status) {
        this.id = id;
        this.runnerId = runnerId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public int getRunnerId() { return runnerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}
