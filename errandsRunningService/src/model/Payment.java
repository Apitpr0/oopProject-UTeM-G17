package model;

import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private int quoteId;
    private double amount;
    private String method;
    private String transactionId;
    private String status;
    private Timestamp createdAt;
    private String confirmedBy;
    private Timestamp confirmedAt;
    private String receiptUrl;
    private String paymentMethod;
    private String paymentStatus;



    // ✅ No-arg constructor (fixes your error)
    public Payment() {}

    // ✅ All-arg constructor
    public Payment(int paymentId, int quoteId, double amount,
                   String method, String transactionId, String status,
                   Timestamp createdAt, String confirmedBy, Timestamp confirmedAt) {
        this.paymentId = paymentId;
        this.quoteId = quoteId;
        this.amount = amount;
        this.method = method;
        this.transactionId = transactionId;
        this.status = status;
        this.createdAt = createdAt;
        this.confirmedBy = confirmedBy;
        this.confirmedAt = confirmedAt;
    }

    // ✅ Getters & Setters
    public String getPaymentMethod() { return paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }

    public Timestamp getPaymentDate() {
        return createdAt;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Timestamp getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Timestamp confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
