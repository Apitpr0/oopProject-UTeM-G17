package model;

public class Errand {
    private int errandId;
    private int customerId;
    private int runnerId; // optional: -1 if unassigned
    private String title;
    private String description;
    private String status; // pending, assigned, completed

    public Errand(int errandId, int customerId, int runnerId, String title, String description, String status) {
        this.errandId = errandId;
        this.customerId = customerId;
        this.runnerId = runnerId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getErrandId() { return errandId; }
    public int getCustomerId() { return customerId; }
    public int getRunnerId() { return runnerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    public void setRunnerId(int runnerId) { this.runnerId = runnerId; }
    public void setStatus(String status) { this.status = status; }
}
