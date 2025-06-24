package DTO;

public class QuoteRequest {
    private int requestId;

    public QuoteRequest(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}