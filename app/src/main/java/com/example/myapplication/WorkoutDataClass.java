package com.example.myapplication;

public class WorkoutDataClass {
    private String workoutTitle;
    private String workoutDesc;
    private String workoutLang;
    private String workoutImage;
    private String difficulty;
    private String duration;
    private String key;

    public WorkoutDataClass() {
        // Required empty constructor for Firebase
    }

    public WorkoutDataClass(String workoutTitle, String workoutDesc, String workoutLang, String workoutImage, String difficulty, String duration) {
        this.workoutTitle = workoutTitle;
        this.workoutDesc = workoutDesc;
        this.workoutLang = workoutLang;
        this.workoutImage = workoutImage;
        this.difficulty = difficulty;
        this.duration = duration;
    }

    public String getWorkoutTitle() {
        return workoutTitle;
    }

    public String getWorkoutDesc() {
        return workoutDesc;
    }

    public String getWorkoutLang() {
        return workoutLang;
    }

    public String getWorkoutImage() {
        return workoutImage;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDuration() {
        return duration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
