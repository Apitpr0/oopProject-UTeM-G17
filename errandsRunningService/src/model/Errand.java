package model;

public class Errand {
    private int id;
    private String type;
    private String description;
    private String pickupAddress;
    private String dropoffAddress;
    private String status;
    private int assignedRunnerId; // Reference to Runner

    // Constructor for creating new Errand (without ID)
    public Errand(String type, String description, String pickupAddress, String dropoffAddress) {
        this.type = type;
        this.description = description;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.status = "Submitted";
        this.assignedRunnerId = -1; // -1 means not assigned yet
    }

    // Constructor with all fields (e.g., from DB)
    public Errand(int id, String type, String description, String pickupAddress, String dropoffAddress, String status, int assignedRunnerId) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.status = status;
        this.assignedRunnerId = assignedRunnerId;
    }

    // === Getters & Setters ===

    public int getId() { return id; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDropoffAddress() { return dropoffAddress; }
    public String getStatus() { return status; }
    public int getAssignedRunnerId() { return assignedRunnerId; }

    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }
    public void setStatus(String status) { this.status = status; }
    public void assignRunner(int runnerId) { this.assignedRunnerId = runnerId; }
}
