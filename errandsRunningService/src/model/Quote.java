package model;

import java.sql.Timestamp;

public class Quote {
    private int quoteId;
    private int requestId;
    private int pricingModelId;
    private double baseFee;
    private double distanceFee;
    private double urgencyMultiplier;
    private double additionalCharges;
    private double subtotal;
    private double taxAmount;
    private double totalAmount;
    private String quoteStatus;
    private Timestamp validUntil;
    private Timestamp acceptedAt;
    private Timestamp createdAt;

    // Constructor
    public Quote(int quoteId, int requestId, int pricingModelId, double baseFee,
                 double distanceFee, double urgencyMultiplier, double additionalCharges,
                 double subtotal, double taxAmount, double totalAmount, String quoteStatus,
                 Timestamp validUntil, Timestamp acceptedAt, Timestamp createdAt) {
        this.quoteId = quoteId;
        this.requestId = requestId;
        this.pricingModelId = pricingModelId;
        this.baseFee = baseFee;
        this.distanceFee = distanceFee;
        this.urgencyMultiplier = urgencyMultiplier;
        this.additionalCharges = additionalCharges;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.quoteStatus = quoteStatus;
        this.validUntil = validUntil;
        this.acceptedAt = acceptedAt;
        this.createdAt = createdAt;
    }

    // Getters
    public int getQuoteId() { return quoteId; }
    public int getRequestId() { return requestId; }
    public int getPricingModelId() { return pricingModelId; }
    public double getBaseFee() { return baseFee; }
    public double getDistanceFee() { return distanceFee; }
    public double getUrgencyMultiplier() { return urgencyMultiplier; }
    public double getAdditionalCharges() { return additionalCharges; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotalAmount() { return totalAmount; }
    public String getQuoteStatus() { return quoteStatus; }
    public Timestamp getValidUntil() { return validUntil; }
    public Timestamp getAcceptedAt() { return acceptedAt; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Helper method for creating a copy with new ID
    public Quote withId(int newId) {
        return new Quote(
                newId, requestId, pricingModelId, baseFee, distanceFee, urgencyMultiplier,
                additionalCharges, subtotal, taxAmount, totalAmount, quoteStatus,
                validUntil, acceptedAt, createdAt
        );
    }
}