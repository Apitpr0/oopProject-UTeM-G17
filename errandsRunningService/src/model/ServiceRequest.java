package model;

import util.DBConnection;

import java.sql.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequest {
    private int id;
    private int customerId;
    private String taskDescription;
    private String status;
    private String pickupAddress;
    private String deliveryAddress;
    private String urgency;
    private double additionalCharge;
    private Integer assignedRunnerId; // Changed to Integer to match NULLable in DB
    private String runnerName; // Additional field not in DB
    private Timestamp createdAt; // Added to match DB field
    private int rating;
    private int runnerId;
    private int taskId;
    // Empty constructor for flexible object creation
    public ServiceRequest() {
        this.id = 0;
        this.status = "Submitted";
        this.urgency = "Normal";
        this.additionalCharge = 0.00;
        this.assignedRunnerId = 0;
        this.runnerName = "Not assigned";
    }

    // Constructor for basic request creation (matches RequestDAO usage)
    public ServiceRequest(int customerId, String taskDescription,
                          String pickupAddress, String deliveryAddress) {
        this();
        this.customerId = customerId;
        this.taskDescription = taskDescription;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
    }

    // Full constructor for database operations
    public ServiceRequest(int id, int customerId, String taskDescription, String status,
                          String pickupAddress, String deliveryAddress, String urgency,
                          double additionalCharge, int assignedRunnerId) {
        this();
        this.id = id;
        this.customerId = customerId;
        this.taskDescription = taskDescription;
        this.status = status;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.urgency = urgency;
        this.additionalCharge = additionalCharge;
        this.assignedRunnerId = assignedRunnerId;
    }

    // Getters
    public int getRating() { return rating; }
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public String getTaskDescription() { return taskDescription; }
    public String getStatus() { return status; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getUrgency() { return urgency; }
    public double getAdditionalCharge() { return additionalCharge; }
    public int getAssignedRunnerId() { return assignedRunnerId; }
    public String getRunnerName() { return runnerName; }
    public int getRunnerId() { return runnerId; }
    public int getTaskId() { return taskId; }

    // Setters
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public void setId(int id) { this.id = id; }
    public void setRating(int rating) { this.rating = rating; }
    public void setRunnerId(int runnerId) { this.runnerId = runnerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public void setStatus(String status) { this.status = status; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public void setAdditionalCharge(double additionalCharge) { this.additionalCharge = additionalCharge; }
    public void setAssignedRunnerId(int assignedRunnerId) { this.assignedRunnerId = assignedRunnerId; }
    public void setRunnerName(String runnerName) { this.runnerName = runnerName; }




}