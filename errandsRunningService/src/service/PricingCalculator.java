package service;

public class PricingCalculator {
    // Urgency multipliers as constants
    private static final double NORMAL_MULTIPLIER = 1.0;
    private static final double EXPRESS_MULTIPLIER = 1.3;
    private static final double EMERGENCY_MULTIPLIER = 1.8;

    /**
     * Calculates distance-based charge
     * @param distanceKm Distance in kilometers
     * @param perKmRate Rate per kilometer
     * @return Calculated distance charge
     */
    public double calculateDistanceCharge(double distanceKm, double perKmRate) {
        if (distanceKm < 0 || perKmRate < 0) {
            throw new IllegalArgumentException("Distance and rate must be positive values");
        }
        return distanceKm * perKmRate;
    }

    /**
     * Gets urgency multiplier for a service level
     * @param urgencyLevel Service urgency level ("Normal", "Express", "Emergency")
     * @return Multiplier value
     */
    public double getUrgencyMultiplier(String urgencyLevel) {
        if (urgencyLevel == null) {
            return NORMAL_MULTIPLIER;
        }

        switch (urgencyLevel.toLowerCase()) {
            case "express":
                return EXPRESS_MULTIPLIER;
            case "emergency":
                return EMERGENCY_MULTIPLIER;
            default:
                return NORMAL_MULTIPLIER;
        }
    }

    /**
     * Calculates tax amount
     * @param subtotal Amount before tax
     * @param taxRate Tax rate (e.g., 0.08 for 8%)
     * @return Calculated tax amount
     */
    public double calculateTax(double subtotal, double taxRate) {
        if (subtotal < 0 || taxRate < 0) {
            throw new IllegalArgumentException("Subtotal and tax rate must be positive values");
        }
        return subtotal * taxRate;
    }
}