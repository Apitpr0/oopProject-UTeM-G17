package DTO;

import java.sql.Timestamp;

public class QuoteResponse {
    private boolean success;
    private String message;
    private int quoteId;
    private double totalAmount;
    private Timestamp validUntil;

    public QuoteResponse(boolean success, String message, int quoteId,
                         double totalAmount, Timestamp validUntil) {
        this.success = success;
        this.message = message;
        this.quoteId = quoteId;
        this.totalAmount = totalAmount;
        this.validUntil = validUntil;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getQuoteId() { return quoteId; }
    public double getTotalAmount() { return totalAmount; }
    public Timestamp getValidUntil() { return validUntil; }
}