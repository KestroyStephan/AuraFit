// StepData.java - Data class for storing step history
package com.example.myapplication;

public class StepData {
    private int steps;
    private int dailyLimit;

    public StepData() {
        // Default constructor required for Firebase
    }

    public StepData(int steps, int dailyLimit) {
        this.steps = steps;
        this.dailyLimit = dailyLimit;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
