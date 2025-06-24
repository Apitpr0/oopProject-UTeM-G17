package service;

import java.sql.*;
import model.Quote;
import model.CustomerRequest;
import model.PricingModel;
import Repository.CustomerRequestRepository;
import Repository.PricingModelRepository;
import Repository.QuoteRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class QuoteService {
    private final Connection connection;
    private final CustomerRequestRepository requestRepository;
    private final PricingModelRepository pricingModelRepository;
    private final QuoteRepository quoteRepository;
    private final PricingCalculator pricingCalculator;

    public QuoteService(Connection connection) {
        this.connection = connection;
        this.requestRepository = new CustomerRequestRepository(connection);
        this.pricingModelRepository = new PricingModelRepository(connection);
        this.quoteRepository = new QuoteRepository(connection);
        this.pricingCalculator = new PricingCalculator();
    }

    public Quote generateQuote(int requestId) throws SQLException {
        // Get request details
        CustomerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new SQLException("Request not found"));

        // Get pricing model
        PricingModel pricingModel = pricingModelRepository.findActive()
                .orElseThrow(() -> new SQLException("No active pricing model found"));

        // Calculate distance (simplified - implement your actual logic)
        double distanceKm = calculateDistance(request.getPickupAddress(), request.getDeliveryAddress());

        // Calculate charges
        double baseFee = pricingModel.getBaseFee();
        double distanceFee = pricingCalculator.calculateDistanceCharge(distanceKm, pricingModel.getPerKmRate());
        double urgencyMultiplier = pricingCalculator.getUrgencyMultiplier(
                request.getUrgency()
        );
        double additionalCharges = request.getAdditionalCharge();

        // Calculate totals
        double subtotal = (baseFee + distanceFee) * urgencyMultiplier + additionalCharges;
        double taxAmount = pricingCalculator.calculateTax(subtotal, 0.10); // 10% tax
        double totalAmount = subtotal + taxAmount;

        // Create and save quote
        Timestamp validUntil = Timestamp.valueOf(LocalDateTime.now().plusHours(24));
        Quote quote = new Quote(
                0, // ID will be generated
                requestId,
                pricingModel.getModelId(),
                baseFee,
                distanceFee,
                urgencyMultiplier,
                additionalCharges,
                subtotal,
                taxAmount,
                totalAmount,
                "draft",
                validUntil,
                null,
                null
        );

        int quoteId = quoteRepository.save(quote);
        return quote.withId(quoteId);
    }

    private double calculateDistance(String pickupAddress, String deliveryAddress) {
        // Implement your actual distance calculation logic
        return 10.0; // Simplified example
    }
}