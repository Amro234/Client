package com.mycompany.client.gameLobby.networking.model.user;

import org.json.JSONObject;

public class OnlineUser {
    private final int userId;
    private final String username;
    private final String email;
    private final int score;
    private final boolean isInGame;

    public OnlineUser(int userId, String username, String email, int score, boolean isInGame) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.score = score;
        this.isInGame = isInGame;
    }

    public static OnlineUser fromJson(JSONObject json) {
        return new OnlineUser(
                json.getInt("userId"),
                json.getString("username"),
                json.getString("email"),
                json.getInt("score"),
                json.getBoolean("isInGame"));
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return score;
    }

    public boolean isInGame() {
        return isInGame;
    }

    @Override
    public String toString() {
        return "OnlineUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", score=" + score +
                ", isInGame=" + isInGame +
                '}';
    }
}
