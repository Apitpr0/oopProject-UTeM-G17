package model;

public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected String role;  // Role field (e.g., "runner", "customer")

    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Abstract methods
    public abstract boolean login(String email, String password);
    public abstract void logout();

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Setters (include only those you intend to allow updates for)
    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Optional setters (use if needed elsewhere)
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
