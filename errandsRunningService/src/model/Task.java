package model;

import java.sql.Timestamp;

public class Task {
    private int id;
    private String status; // pending, in progress, completed
    private int customerId;
    private int runnerId;
    private String runnerName;
    private String taskDescription;
    private String pickupAddress;
    private String deliveryAddress;
    private String urgency;
    private Timestamp updatedAt;

    // Constructor
    public Task(int id, String status, int customerId, int runnerId, String runnerName,
                String taskDescription, String pickupAddress, String deliveryAddress,
                String urgency, Timestamp updatedAt) {
        this.id = id;
        this.status = status;
        this.customerId = customerId;
        this.runnerId = runnerId;
        this.runnerName = runnerName;
        this.taskDescription = taskDescription;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.urgency = urgency;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getRunnerId() { return runnerId; }
    public void setRunnerId(int runnerId) { this.runnerId = runnerId; }
    public String getRunnerName() { return runnerName; }
    public void setRunnerName(String runnerName) { this.runnerName = runnerName; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}