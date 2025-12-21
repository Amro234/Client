package com.mycompany.client.gameLobby.networking.model.challenge;

public class ChallengeResponse {
    private final boolean success;
    private final String message;

    public ChallengeResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
