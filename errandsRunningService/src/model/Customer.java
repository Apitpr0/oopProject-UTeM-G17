package model;

public class Customer extends User {
    public Customer(int id, String name, String email, String password) {
        super(id, name, email, password, "customer"); // Pass role explicitly
    }

    @Override
    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    @Override
    public void logout() {
        System.out.println(name + " logged out.");
    }
}
