package model;

public class UrgentServiceRequest extends ServiceRequest {
    private static final double EXTRA_CHARGE = 10.00;

    public UrgentServiceRequest(int customerId, String taskDescription, String pickupAddress, String deliveryAddress) {
        // ✅ Call the correct super constructor with 4 arguments
        super(customerId, taskDescription, pickupAddress, deliveryAddress);

        // ✅ Set urgency and extra charge
        setUrgency("Urgent");
        setAdditionalCharge(EXTRA_CHARGE);
    }

    public static double getExtraCharge() {
        return EXTRA_CHARGE;
    }
}
