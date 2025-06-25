package service;

import model.Payment;
import Repository.PaymentRepository;

import java.sql.SQLException;
import java.util.Scanner;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private static final Scanner scanner = new Scanner(System.in);

    // Available digital wallets
    private static final String[] DIGITAL_WALLETS = {"Tng", "GooglePay", "Setel", "MAE"};

    // Available banks
    private static final String[] BANKS = {
            "Bank Islam", "Maybank", "CIMB Bank", "Public Bank", "Affin Bank",
            "Hong Leong Bank", "RHB Bank", "Agro Bank", "Alliance Bank",
            "AmBank", "Bank Muamalat", "Bank Simpanan Nasional", "HSBC"
    };

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 🔁 CLI-based version (interactive)
    public Payment processInteractivePayment(int quoteId, double amount) throws SQLException {
        System.out.println("\nAvailable payment methods:");
        System.out.println("1. Credit Card");
        System.out.println("2. Digital Wallet");
        System.out.println("3. Bank Transfer");

        int choice = getValidInt("Select payment method (1-3): ", 1, 3);
        String paymentMethod = "";
        String transactionId = "TXN" + System.currentTimeMillis();

        switch (choice) {
            case 1 -> {
                String cardNumber = getNumeric("Enter card number (16 digits): ", 16);
                String expiry = getExpiryDate("Enter expiry date (MM/YY): ");
                String cvv = getNumeric("Enter CVV (3 digits): ", 3);
                paymentMethod = "Credit Card ending with " + cardNumber.substring(cardNumber.length() - 4);
            }
            case 2 -> {
                String wallet = selectOption("Select digital wallet:", DIGITAL_WALLETS);
                String walletId = getNumeric("Enter wallet ID (8-20 digits): ", 8, 20);
                paymentMethod = wallet + " (" + walletId + ")";
            }
            case 3 -> {
                String bank = selectOption("Select bank:", BANKS);
                String account = getNumeric("Enter account number (8-20 digits): ", 8, 20);
                paymentMethod = bank + " Account";
            }
        }

        Payment payment = new Payment(
                0,
                quoteId,
                amount,
                paymentMethod,
                transactionId,
                "pending",
                null,
                null,
                null
        );

        int paymentId = paymentRepository.processPayment(payment);
        return new Payment(
                paymentId,
                payment.getQuoteId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaymentStatus(),
                payment.getPaymentDate(),
                payment.getReceiptUrl(),
                payment.getCreatedAt()
        );
    }

    // ✅ Added this method back for controller/GUI compatibility
    public Payment processPayment(int quoteId, double amount, String paymentMethod, String transactionId) throws SQLException {
        Payment payment = new Payment(
                0,
                quoteId,
                amount,
                paymentMethod,
                transactionId,
                "pending",
                null,
                null,
                null
        );

        int paymentId = paymentRepository.processPayment(payment);
        return new Payment(
                paymentId,
                payment.getQuoteId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaymentStatus(),
                payment.getPaymentDate(),
                payment.getReceiptUrl(),
                payment.getCreatedAt()
        );
    }

    public boolean confirmPayment(int paymentId) throws SQLException {
        return paymentRepository.updatePaymentStatus(paymentId, "completed");
    }

    public boolean failPayment(int paymentId) throws SQLException {
        return paymentRepository.updatePaymentStatus(paymentId, "failed");
    }

    // === Helper Methods ===
    private static int getValidInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Please enter a number between %d and %d.\n", min, max);
        }
    }

    private static String getNumeric(String prompt, int exactLength) {
        return getNumeric(prompt, exactLength, exactLength);
    }

    private static String getNumeric(String prompt, int minLen, int maxLen) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.matches("\\d{" + minLen + "," + maxLen + "}")) return input;
            System.out.printf("Please enter digits only (%d–%d characters).\n", minLen, maxLen);
        }
    }

    private static String getExpiryDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.matches("(0[1-9]|1[0-2])/\\d{2}")) return input;
            System.out.println("Invalid format. Use MM/YY.");
        }
    }

    private static String selectOption(String prompt, String[] options) {
        System.out.println(prompt);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s\n", i + 1, options[i]);
        }
        int choice = getValidInt("Choose option: ", 1, options.length);
        return options[choice - 1];
    }
}
