package model;

public class Runner extends User {
    public Runner(int id, String name, String email, String password) {
        super(id, name, email, password, "runner"); // ✅ hardcoded "runner"
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
