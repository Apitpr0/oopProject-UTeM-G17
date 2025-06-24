package model;

import java.sql.Timestamp;

public class PricingModel {
    private int modelId;
    private String modelName;
    private double baseFee;
    private double perKmRate;
    private double minFee;
    private String urgencySurcharge;
    private String timeSurcharge;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor
    public PricingModel(int modelId, String modelName, double baseFee, double perKmRate,
                        double minFee, String urgencySurcharge, String timeSurcharge,
                        boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.baseFee = baseFee;
        this.perKmRate = perKmRate;
        this.minFee = minFee;
        this.urgencySurcharge = urgencySurcharge;
        this.timeSurcharge = timeSurcharge;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public double getBaseFee() { return baseFee; }
    public double getPerKmRate() { return perKmRate; }
    public double getMinFee() { return minFee; }
    public String getUrgencySurcharge() { return urgencySurcharge; }
    public String getTimeSurcharge() { return timeSurcharge; }
    public boolean isActive() { return isActive; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
}