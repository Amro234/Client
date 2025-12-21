package com.mycompany.client.gameLobby.networking.model.challenge;

public class AcceptChallengeResponse {
    private final boolean success;
    private final String message;
    private final Challenge opponent;

    public AcceptChallengeResponse(boolean success, String message, Challenge opponent) {
        this.success = success;
        this.message = message;
        this.opponent = opponent;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Challenge getOpponent() {
        return opponent;
    }
}
