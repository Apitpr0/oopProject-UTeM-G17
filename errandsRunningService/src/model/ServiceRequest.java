package model;

public class ServiceRequest {
    private int id;
    private int customerId;
    private String taskDescription;
    private String status;
    private String pickupAddress;
    private String deliveryAddress;
    private String urgency;
    private double additionalCharge;

    // Constructor for new requests (before inserting into DB)
    public ServiceRequest(int customerId, String taskDescription, String pickupAddress, String deliveryAddress) {
        this.customerId = customerId;
        this.taskDescription = taskDescription;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.status = "Submitted";
        this.urgency = "Normal";
        this.additionalCharge = 0.0;
    }


    // Constructor for full object (e.g., when loading from DB)
    public ServiceRequest(int id, int customerId, String taskDescription, String status,
                          String pickupAddress, String deliveryAddress, String urgency, double additionalCharge) {
        this.id = id;
        this.customerId = customerId;
        this.taskDescription = taskDescription;
        this.status = status;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.urgency = urgency;
        this.additionalCharge = additionalCharge;
    }

    // === Getters ===
    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getStatus() {
        return status;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getUrgency() {
        return urgency;
    }

    public double getAdditionalCharge() {
        return additionalCharge;
    }

    // === Setters ===
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public void setAdditionalCharge(double additionalCharge) {
        this.additionalCharge = additionalCharge;
    }
}
