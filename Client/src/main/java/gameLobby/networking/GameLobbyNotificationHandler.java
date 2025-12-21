package gameLobby.networking;

import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.core.server.ServerConnection.ServerMessageListener;
import gameLobby.networking.model.notification.ChallengeAcceptedNotification;
import gameLobby.networking.model.notification.ChallengeDeclinedNotification;
import gameLobby.networking.model.notification.ChallengeReceivedNotification;
import javafx.application.Platform;
import org.json.JSONObject;

/**
 * Handles incoming notifications from the server for game lobby events
 */
public class GameLobbyNotificationHandler implements ServerMessageListener {

    private GameLobbyNotificationListener listener;

    public GameLobbyNotificationHandler() {
        // Register this handler with ServerConnection
        ServerConnection.setMessageListener(this);
    }

    /**
     * Set the listener for game lobby notifications
     */
    public void setNotificationListener(GameLobbyNotificationListener listener) {
        this.listener = listener;
    }

    /**
     * Remove the notification listener
     */
    public void clearNotificationListener() {
        this.listener = null;
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.optString("type", "UNKNOWN");

            System.out.println("[GameLobby] Received notification: " + type);

            switch (type) {
                case "CHALLENGE_RECEIVED":
                    handleChallengeReceived(json);
                    break;

                case "CHALLENGE_ACCEPTED":
                    handleChallengeAccepted(json);
                    break;

                case "CHALLENGE_DECLINED":
                    handleChallengeDeclined(json);
                    break;

                default:
                    System.out.println("[GameLobby] Unknown notification type: " + type);
                    break;
            }

        } catch (Exception e) {
            System.err.println("[GameLobby] Error processing notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionLost() {
        System.err.println("[GameLobby] Connection to server lost");
        // You can notify the UI here if needed
    }

    private void handleChallengeReceived(JSONObject json) {
        if (listener == null) {
            System.out.println("[GameLobby] No listener set for CHALLENGE_RECEIVED");
            return;
        }

        ChallengeReceivedNotification notification = ChallengeReceivedNotification.fromJson(json);
        System.out.println("[GameLobby] Challenge received from: " + notification.getChallengerUsername());

        // Execute on JavaFX Application Thread for UI updates
        Platform.runLater(() -> listener.onChallengeReceived(notification));
    }

    private void handleChallengeAccepted(JSONObject json) {
        if (listener == null) {
            System.out.println("[GameLobby] No listener set for CHALLENGE_ACCEPTED");
            return;
        }

        ChallengeAcceptedNotification notification = ChallengeAcceptedNotification.fromJson(json);
        System.out.println("[GameLobby] Challenge accepted by: " + notification.getAcceptedByUsername());

        // Execute on JavaFX Application Thread for UI updates
        Platform.runLater(() -> listener.onChallengeAccepted(notification));
    }

    private void handleChallengeDeclined(JSONObject json) {
        if (listener == null) {
            System.out.println("[GameLobby] No listener set for CHALLENGE_DECLINED");
            return;
        }

        ChallengeDeclinedNotification notification = ChallengeDeclinedNotification.fromJson(json);
        System.out.println("[GameLobby] Challenge declined by: " + notification.getDeclinedByUsername());

        // Execute on JavaFX Application Thread for UI updates
        Platform.runLater(() -> listener.onChallengeDeclined(notification));
    }
}
