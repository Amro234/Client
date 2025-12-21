package com.mycompany.client.gameLobby.networking.model.notification;

import org.json.JSONObject;

public class ChallengeDeclinedNotification {
    private final int declinedBy;
    private final String declinedByUsername;
    private final String message;

    public ChallengeDeclinedNotification(int declinedBy, String declinedByUsername, String message) {
        this.declinedBy = declinedBy;
        this.declinedByUsername = declinedByUsername;
        this.message = message;
    }

    public static ChallengeDeclinedNotification fromJson(JSONObject json) {
        return new ChallengeDeclinedNotification(
                json.getInt("declinedBy"),
                json.getString("declinedByUsername"),
                json.getString("message"));
    }

    public int getDeclinedBy() {
        return declinedBy;
    }

    public String getDeclinedByUsername() {
        return declinedByUsername;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChallengeDeclinedNotification{" +
                "declinedBy=" + declinedBy +
                ", declinedByUsername='" + declinedByUsername + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
