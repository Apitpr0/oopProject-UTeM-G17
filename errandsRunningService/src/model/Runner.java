package model;

public class Runner extends User {
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public Runner(int id, String name, String email, String password,
                  String dayOfWeek, String startTime, String endTime) {
        super(id, name, email, password, "runner");
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public String getDayOfWeek() { return dayOfWeek != null ? dayOfWeek : "-"; }
    public String getStartTime() { return startTime != null ? startTime : "-"; }
    public String getEndTime() { return endTime != null ? endTime : "-"; }

    // Setters
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}