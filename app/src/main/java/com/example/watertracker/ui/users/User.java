package com.example.watertracker.ui.users;

public class User {

    private final String name;
    private final String email;
    private final String status;
    private final String initials;

    public User(String name, String email, String status, String initials) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.initials = initials;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getInitials() {
        return initials;
    }
}