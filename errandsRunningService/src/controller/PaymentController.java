package controller;

import model.Payment;
import service.PaymentService;
import java.sql.SQLException;

public class PaymentController {
    private final PaymentService paymentService;
//
//    public PaymentController(){
//
//    }

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Simulate a successful payment without touching the database.
     */
    public Payment processPayment(int quoteId,
                                  double amount,
                                  String paymentMethod,
                                  String transactionId) {
        // 🔧 Skip service layer & DB logic, simulate success directly
        Payment payment = new Payment();
        payment.setPaymentId(999); // Dummy ID
        payment.setAmount(amount);
        payment.setMethod(paymentMethod);
        payment.setStatus("Success");

        System.out.println("✅ Simulated payment processed:");
        System.out.println(" - quoteId: " + quoteId);
        System.out.println(" - amount: RM" + amount);
        System.out.println(" - method: " + paymentMethod);
        System.out.println(" - transactionId: " + transactionId);

        return payment;
    }

    /**
     * Always confirm payment successfully.
     */
    public boolean confirmPayment(int paymentId) {
        System.out.println("✅ Payment ID " + paymentId + " confirmed successfully.");
        return true;
    }

    /**
     * Optional: Always fail gracefully (if used).
     */
    public boolean failPayment(int paymentId) {
        System.out.println("❌ Payment ID " + paymentId + " marked as failed.");
        return true;
    }
}
