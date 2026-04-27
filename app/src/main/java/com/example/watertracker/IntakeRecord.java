package com.example.watertracker;

public class IntakeRecord {

    private String date;
    private int waterMl;
    private int goalMl;

    public IntakeRecord() {
    }

    public IntakeRecord(String date, int waterMl, int goalMl) {
        this.date = date;
        this.waterMl = waterMl;
        this.goalMl = goalMl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWaterMl() {
        return waterMl;
    }

    public void setWaterMl(int waterMl) {
        this.waterMl = waterMl;
    }

    public int getGoalMl() {
        return goalMl;
    }

    public void setGoalMl(int goalMl) {
        this.goalMl = goalMl;
    }
}