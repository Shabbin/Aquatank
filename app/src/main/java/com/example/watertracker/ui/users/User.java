package com.example.watertracker.ui.users;

public class User {

    private int id;
    private String name;
    private String email;
    private String status;
    private String initials;
    private double currentLiters;
    private double goalLiters;

    public User() {
    }

    // Old database-style constructor
    public User(String name, String email, String status, String initials) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.initials = initials;
        this.currentLiters = 0.0;
        this.goalLiters = 0.0;
    }

    // Old database read constructor
    public User(int id, String name, String email, String status, String initials) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.initials = initials;
        this.currentLiters = 0.0;
        this.goalLiters = 0.0;
    }

    // New community progress constructor
    public User(String name, String initials, double currentLiters, double goalLiters) {
        this.name = name;
        this.initials = initials;
        this.currentLiters = currentLiters;
        this.goalLiters = goalLiters;
        this.email = "";
        this.status = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public String getInitials() {
        return initials == null ? "" : initials;
    }

    public double getCurrentLiters() {
        return currentLiters;
    }

    public double getGoalLiters() {
        return goalLiters;
    }

    public int getProgressPercent() {
        if (goalLiters <= 0) {
            return 0;
        }

        int percent = (int) Math.round((currentLiters / goalLiters) * 100);
        return Math.min(percent, 100);
    }

    public String getBadgeText() {
        if (goalLiters <= 0) {
            return "Not Started";
        }

        if (currentLiters >= goalLiters) {
            return "Goal Achieved";
        }

        return "In Progress";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public void setCurrentLiters(double currentLiters) {
        this.currentLiters = currentLiters;
    }

    public void setGoalLiters(double goalLiters) {
        this.goalLiters = goalLiters;
    }
}