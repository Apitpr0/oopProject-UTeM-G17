package model;

import java.sql.Time;

public class RunnerAvailability {
    private int id;
    private int runnerId;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;

    public RunnerAvailability(int id, int runnerId, String dayOfWeek, Time startTime, Time endTime) {
        this.id = id;
        this.runnerId = runnerId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRunnerId() {
        return runnerId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setRunnerId(int runnerId) {
        this.runnerId = runnerId;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
