package gameLobby.networking.model.notification;

import org.json.JSONObject;

public class ChallengeAcceptedNotification {
    private final int acceptedBy;
    private final String acceptedByUsername;
    private final String message;

    public ChallengeAcceptedNotification(int acceptedBy, String acceptedByUsername, String message) {
        this.acceptedBy = acceptedBy;
        this.acceptedByUsername = acceptedByUsername;
        this.message = message;
    }

    public static ChallengeAcceptedNotification fromJson(JSONObject json) {
        return new ChallengeAcceptedNotification(
                json.getInt("acceptedBy"),
                json.getString("acceptedByUsername"),
                json.getString("message"));
    }

    public int getAcceptedBy() {
        return acceptedBy;
    }

    public String getAcceptedByUsername() {
        return acceptedByUsername;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChallengeAcceptedNotification{" +
                "acceptedBy=" + acceptedBy +
                ", acceptedByUsername='" + acceptedByUsername + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
