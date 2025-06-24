package Repository;

import java.sql.*;
import model.Quote;
import java.time.LocalDateTime;

public class QuoteRepository {
    private final Connection connection;

    public QuoteRepository(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = connection;
    }

    public int save(Quote quote) throws SQLException {
        if (quote == null) {
            throw new IllegalArgumentException("Quote cannot be null");
        }

        String query = """
            INSERT INTO quotes (
                request_id, pricing_model_id, base_fee, distance_fee,
                urgency_multiplier, additional_charges, subtotal,
                tax_amount, total_amount, valid_until, quote_status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Set parameters
            stmt.setInt(1, quote.getRequestId());
            stmt.setInt(2, quote.getPricingModelId());
            stmt.setDouble(3, quote.getBaseFee());
            stmt.setDouble(4, quote.getDistanceFee());
            stmt.setDouble(5, quote.getUrgencyMultiplier());
            stmt.setDouble(6, quote.getAdditionalCharges());
            stmt.setDouble(7, quote.getSubtotal());
            stmt.setDouble(8, quote.getTaxAmount());
            stmt.setDouble(9, quote.getTotalAmount());

            // Handle Timestamp conversion
            Object validUntil = quote.getValidUntil(); // Correct method name

            if (validUntil instanceof LocalDateTime) {
                stmt.setTimestamp(10, Timestamp.valueOf((LocalDateTime) validUntil));
            } else if (validUntil instanceof Timestamp) {
                stmt.setTimestamp(10, (Timestamp) validUntil);
            } else {
                throw new IllegalArgumentException("Invalid validUntil type: " + validUntil.getClass());
            }

            stmt.setString(11, quote.getQuoteStatus());

            // Execute and get generated ID
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating quote failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating quote failed, no ID obtained.");
                }
            }
        }
    }
}