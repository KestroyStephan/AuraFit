package com.example.myapplication;

public class WorkoutHistory {
    private String id;
    private String dateTime;
    private String duration;

    // Required empty constructor for Firebase
    public WorkoutHistory() {}

    public WorkoutHistory(String id, String dateTime, String duration) {
        this.id = id;
        this.dateTime = dateTime;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
