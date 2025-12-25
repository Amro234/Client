package com.mycompany.client.gameLobby.networking.model.challenge;

public class AcceptChallengeResponse {
    private final boolean success;
    private final String message;
    private final Challenge opponent;
    private final org.json.JSONObject gameDetails;

    public AcceptChallengeResponse(boolean success, String message, Challenge opponent,
            org.json.JSONObject gameDetails) {
        this.success = success;
        this.message = message;
        this.opponent = opponent;
        this.gameDetails = gameDetails;
    }

    public org.json.JSONObject getGameDetails() {
        return gameDetails;
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
