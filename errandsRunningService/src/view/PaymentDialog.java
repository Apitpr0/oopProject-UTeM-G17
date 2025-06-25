package view;

import model.Payment;
import controller.PaymentController;
import javax.swing.*;
import java.awt.*;

import java.text.DecimalFormat;

public class PaymentDialog {
    private PaymentController paymentController;
    private JFrame parentFrame;
    private Runnable refreshCallback;


    public PaymentDialog(JFrame parentFrame, PaymentController paymentController, Runnable refreshCallback) {
        this.parentFrame = parentFrame;
        this.paymentController = paymentController;
        this.refreshCallback = refreshCallback;
    }

    public void showPaymentDialog(int requestId, double amount) {
        JPanel paymentPanel = createPaymentPanel();

        int result = JOptionPane.showConfirmDialog(
                parentFrame,
                paymentPanel,
                "Payment for Request #" + requestId + " - RM" + String.format("%.2f", amount),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String method = getSelectedPaymentMethod(paymentPanel);
            if (validatePaymentInput(paymentPanel, method)) {
                processPayment(requestId, amount, method, paymentPanel);
            }
        }
    }

    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setPreferredSize(new Dimension(450, 400));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Payment Method Selection
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.add(new JLabel("Payment Method:"));
        JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{
                "Credit Card", "Digital Wallet", "Bank Transfer"
        });
        methodPanel.add(paymentMethodCombo);
        paymentPanel.add(methodPanel);

        // Create payment method panels
        JPanel cardPanel = createCreditCardPanel();
        JPanel walletPanel = createDigitalWalletPanel();
        JPanel bankPanel = createBankTransferPanel();

        paymentPanel.add(cardPanel);
        paymentPanel.add(walletPanel);
        paymentPanel.add(bankPanel);

        // Set initial visibility
        cardPanel.setVisible(true);
        walletPanel.setVisible(false);
        bankPanel.setVisible(false);

        // Add listener for method selection
        paymentMethodCombo.addActionListener(e -> {
            String method = (String) paymentMethodCombo.getSelectedItem();
            cardPanel.setVisible("Credit Card".equals(method));
            walletPanel.setVisible("Digital Wallet".equals(method));
            bankPanel.setVisible("Bank Transfer".equals(method));
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });

        return paymentPanel;
    }

    private JPanel createCreditCardPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Credit Card Details"));

        JTextField cardNumberField = new JTextField();
        cardNumberField.setName("cardNumber");
        JTextField expiryField = new JTextField();
        expiryField.setName("expiry");
        JTextField cvvField = new JTextField();
        cvvField.setName("cvv");

        panel.add(new JLabel("Card Number (16-20 digits):"));
        panel.add(cardNumberField);
        panel.add(new JLabel("Expiry (MM/YY):"));
        panel.add(expiryField);
        panel.add(new JLabel("CVV (3 digits):"));
        panel.add(cvvField);

        return panel;
    }

    private JPanel createDigitalWalletPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Digital Wallet"));

        JComboBox<String> walletCombo = new JComboBox<>(new String[]{"TNG", "GrabPay", "ShopeePay", "Boost"});
        walletCombo.setName("walletType");

        JTextField phoneField = new JTextField();
        phoneField.setName("walletPhone");
        phoneField.setPreferredSize(new Dimension(200, 25));

        JTextField verifyCode = new JTextField();
        verifyCode.setName("walletVerifyCode");
        verifyCode.setPreferredSize(new Dimension(200, 25));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Select Wallet:"));
        row1.add(walletCombo);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Phone (10 digits):"));
        row2.add(phoneField);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Verification Code (6 digits):"));
        row3.add(verifyCode);

        panel.add(row1);
        panel.add(row2);
        panel.add(row3);

        return panel;
    }


    private JPanel createBankTransferPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Bank Transfer"));

        JComboBox<String> bankCombo = new JComboBox<>(new String[]{
                "Bank Islam", "Maybank", "CIMB Bank", "Public Bank", "Affin Bank",
                "Hong Leong Bank", "RHB Bank", "Agro Bank", "Alliance Bank",
                "AmBank", "Bank Muamalat", "Bank Simpanan Nasional", "HSBC"
        });
        bankCombo.setName("bankName");

        JTextField accountField = new JTextField();
        accountField.setName("accountNumber");
        accountField.setPreferredSize(new Dimension(200, 25));

        panel.add(new JLabel("Select Bank:"));
        panel.add(bankCombo);
        panel.add(new JLabel("Account Number (18-20 digits):"));
        panel.add(accountField);

        return panel;
    }


    private String getSelectedPaymentMethod(JPanel paymentPanel) {
        JComboBox<String> combo = (JComboBox<String>) ((JPanel) paymentPanel.getComponent(0)).getComponent(1);
        return (String) combo.getSelectedItem();
    }

    private boolean validatePaymentInput(JPanel paymentPanel, String method) {
        if ("Credit Card".equals(method)) {
            JTextField cardNumber = (JTextField) findComponentByName((JPanel) paymentPanel.getComponent(1), "cardNumber");
            JTextField expiry = (JTextField) findComponentByName((JPanel) paymentPanel.getComponent(1), "expiry");
            JTextField cvv = (JTextField) findComponentByName((JPanel) paymentPanel.getComponent(1), "cvv");

            if (!cardNumber.getText().matches("\\d{16,20}")) {
                showError("Invalid card number. Must be 16-20 digits.");
                return false;
            }
            if (!expiry.getText().matches("\\d{2}/\\d{2}")) {
                showError("Invalid expiry. Use MM/YY format.");
                return false;
            }
            if (!cvv.getText().matches("\\d{3}")) {
                showError("Invalid CVV. Must be 3 digits.");
                return false;
            }
        } else if ("Digital Wallet".equals(method)) {
            JTextField phone = (JTextField) findComponentByName((JPanel) paymentPanel.getComponent(2), "walletPhone");
            JTextField code = (JTextField) findComponentByName((JPanel) paymentPanel.getComponent(2), "walletVerifyCode");

            if (!phone.getText().matches("\\d{10}")) {
                showError("Invalid phone number. Must be 10 digits.");
                return false;
            }
            if (!code.getText().matches("\\d{6}")) {
                showError("Invalid verification code. Must be 6 digits.");
                return false;
            }
        } else if ("Bank Transfer".equals(method)) {
            JTextField account = (JTextField) findComponentByName((JPanel) paymentPanel.getComponent(3), "accountNumber");

            if (!account.getText().matches("\\d{18,20}")) {
                showError("Invalid account number. Must be 18-20 digits.");
                return false;
            }
        }
        return true;
    }

    private Component findComponentByName(JPanel panel, String name) {
        for (Component comp : panel.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }

    private void processPayment(int requestId, double amount, String method, JPanel paymentPanel) {
        try {
            String transactionId = "TXN" + System.currentTimeMillis();

            Payment payment = paymentController.processPayment(
                    requestId,
                    amount,
                    method,
                    transactionId
            );

            boolean success = paymentController.confirmPayment(payment.getPaymentId());
            if (success) {
                showSuccess(
                        "✅ Payment processed successfully!\n\n" +
                                "Quote ID       : " + requestId + "\n" +
                                "Amount         : RM" + new DecimalFormat("0.00").format(amount) + "\n" +
                                "Method         : " + method + "\n" +
                                "Transaction ID : " + transactionId
                );

                refreshCallback.run();
            } else {
                showError("Payment confirmation failed.");
            }
        } catch (Exception e) {
            showError("Payment error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}