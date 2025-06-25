package Repository;

import model.Payment;
import java.sql.*;
import java.sql.SQLException;

public class PaymentRepository {
    private final Connection connection;

    public PaymentRepository(Connection connection) {
        this.connection = connection;
    }

    public int processPayment(Payment payment) throws SQLException {
        String query = """
            INSERT INTO payments (
                quote_id, amount, payment_method, 
                transaction_id, payment_status, receipt_url
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getQuoteId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getTransactionId());
            stmt.setString(5, payment.getPaymentStatus());
            stmt.setString(6, payment.getReceiptUrl());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Payment processing failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Payment processing failed, no ID obtained.");
                }
            }
        }
    }


    public boolean updatePaymentStatus(int paymentId, String status) throws SQLException {
        String query = "UPDATE payments SET payment_status = ?, payment_date = CURRENT_TIMESTAMP WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }
}