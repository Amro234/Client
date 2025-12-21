package com.mycompany.client.auth.model;

public class User {

    private int id;
    private String username;
    private String email;
    private int score;

    public User() {
    }

    public User(int id, String username, String email, int score) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.score = score;
    }

    public User(String username, String email, int score) {
        this.username = username;
        this.email = email;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
