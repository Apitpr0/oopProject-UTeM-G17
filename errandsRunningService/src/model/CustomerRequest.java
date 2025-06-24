package model;

import java.sql.Timestamp;

public class CustomerRequest {
    private int id;
    private int customerId;
    private String taskDescription;
    private String status;
    private Timestamp createdAt;
    private String pickupAddress;
    private String deliveryAddress;
    private String urgency;
    private double additionalCharge;
    private Integer assignedRunnerId;

    // Constructor
    public CustomerRequest(int id, int customerId, String taskDescription, String status,
                           Timestamp createdAt, String pickupAddress, String deliveryAddress,
                           String urgency, double additionalCharge, Integer assignedRunnerId) {
        this.id = id;
        this.customerId = customerId;
        this.taskDescription = taskDescription;
        this.status = status;
        this.createdAt = createdAt;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.urgency = urgency;
        this.additionalCharge = additionalCharge;
        this.assignedRunnerId = assignedRunnerId;
    }

    // Getters
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public String getTaskDescription() { return taskDescription; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getUrgency() { return urgency; }
    public double getAdditionalCharge() { return additionalCharge; }
    public Integer getAssignedRunnerId() { return assignedRunnerId; }
}