package com.mycompany.client.gameLobby.networking.model.user;

import org.json.JSONObject;

import com.mycompany.client.gameLobby.enums.PlayerStatus;

public class OnlineUser {
    private final int userId;
    private final String username;
    private final String email;
    private final int score;
    private final PlayerStatus status;

    public OnlineUser(int userId, String username, String email, int score, PlayerStatus status) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.score = score;
        this.status = status;
    }

    public static OnlineUser fromJson(JSONObject json) {
        return new OnlineUser(
                json.getInt("userId"),
                json.getString("username"),
                json.getString("email"),
                json.getInt("score"),
                json.getBoolean("isInGame") ? PlayerStatus.IN_GAME : PlayerStatus.READY);
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

    public PlayerStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "OnlineUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", score=" + score +
                ", status=" + status.name() +
                '}';
    }
}
