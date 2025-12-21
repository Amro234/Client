package com.mycompany.client.gameLobby.networking.model.challenge;

import org.json.JSONObject;

public class Challenge {
    private final int challengerId;
    private final String challengerUsername;
    private final int challengedId;
    private final String challengedUsername;
    private final long timestamp;

    public Challenge(int challengerId, String challengerUsername, int challengedId, String challengedUsername,
            long timestamp) {
        this.challengerId = challengerId;
        this.challengerUsername = challengerUsername;
        this.challengedId = challengedId;
        this.challengedUsername = challengedUsername;
        this.timestamp = timestamp;
    }

    public static Challenge fromJson(JSONObject json) {
        return new Challenge(
                json.getInt("challengerId"),
                json.getString("challengerUsername"),
                json.getInt("challengedId"),
                json.getString("challengedUsername"),
                json.getLong("timestamp"));
    }

    public int getChallengerId() {
        return challengerId;
    }

    public String getChallengerUsername() {
        return challengerUsername;
    }

    public int getChallengedId() {
        return challengedId;
    }

    public String getChallengedUsername() {
        return challengedUsername;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "challengerId=" + challengerId +
                ", challengerUsername='" + challengerUsername + '\'' +
                ", challengedId=" + challengedId +
                ", challengedUsername='" + challengedUsername + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
