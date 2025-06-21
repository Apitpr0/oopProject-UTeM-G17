package model;

public class Runner extends User {
    private String availability; // Optional - summary like "Mon 8-17"

    public Runner(int id, String name, String email, String password, String availability) {
        super(id, name, email, password, "runner");
        this.availability = availability;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}


