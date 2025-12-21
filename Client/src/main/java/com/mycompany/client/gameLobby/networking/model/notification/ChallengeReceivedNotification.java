package com.mycompany.client.gameLobby.networking.model.notification;

import org.json.JSONObject;

public class ChallengeReceivedNotification {
    private final int challengerId;
    private final String challengerUsername;
    private final String message;

    public ChallengeReceivedNotification(int challengerId, String challengerUsername, String message) {
        this.challengerId = challengerId;
        this.challengerUsername = challengerUsername;
        this.message = message;
    }

    public static ChallengeReceivedNotification fromJson(JSONObject json) {
        return new ChallengeReceivedNotification(
                json.getInt("challengerId"),
                json.getString("challengerUsername"),
                json.getString("message"));
    }

    public int getChallengerId() {
        return challengerId;
    }

    public String getChallengerUsername() {
        return challengerUsername;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChallengeReceivedNotification{" +
                "challengerId=" + challengerId +
                ", challengerUsername='" + challengerUsername + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
