package com.example.watertracker.ui.users;

public class User {

    private int id;
    private String name;
    private String email;
    private String status;
    private String initials;

    public User() {
    }

    public User(String name, String email, String status, String initials) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.initials = initials;
    }

    public User(int id, String name, String email, String status, String initials) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.initials = initials;
    }

    public int getId() {
        return id;
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
}