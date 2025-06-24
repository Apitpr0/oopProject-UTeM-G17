package Repository;

import model.PricingModel;
import java.sql.*;
import java.util.Optional;

public class PricingModelRepository {
    private final Connection connection;

    public PricingModelRepository(Connection connection) {
        this.connection = connection;
    }

    public Optional<PricingModel> findActive() throws SQLException {
        String query = "SELECT * FROM pricing_model WHERE is_active = TRUE LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new PricingModel(
                        rs.getInt("model_id"),
                        rs.getString("model_name"),
                        rs.getDouble("base_fee"),
                        rs.getDouble("per_km_rate"),
                        rs.getDouble("min_fee"),
                        rs.getString("urgency_surcharge"),
                        rs.getString("time_surcharge"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
            return Optional.empty();
        }
    }
}