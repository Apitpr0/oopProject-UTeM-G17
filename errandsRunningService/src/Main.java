import view.LoginPage;

public class Main {
    public static void main(String[] args) {
        // Directly launch the LoginPage
        javax.swing.SwingUtilities.invokeLater(() -> new LoginPage(""));
    }
}
