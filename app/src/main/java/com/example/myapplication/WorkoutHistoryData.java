package com.example.myapplication;

public class WorkoutHistoryData {
    private String id;
    private String date;
    private String duration;

    public WorkoutHistoryData() {}  // Required by Firebase

    public WorkoutHistoryData(String id, String date, String duration) {
        this.id = id;
        this.date = date;
        this.duration = duration;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getDuration() { return duration; }
}
