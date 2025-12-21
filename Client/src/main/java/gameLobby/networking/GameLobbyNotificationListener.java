package gameLobby.networking;

import gameLobby.networking.model.notification.ChallengeAcceptedNotification;
import gameLobby.networking.model.notification.ChallengeDeclinedNotification;
import gameLobby.networking.model.notification.ChallengeReceivedNotification;

/**
 * Interface for handling game lobby notifications from the server
 */
public interface GameLobbyNotificationListener {

    /**
     * Called when a challenge is received from another player
     */
    void onChallengeReceived(ChallengeReceivedNotification notification);

    /**
     * Called when your challenge is accepted by another player
     */
    void onChallengeAccepted(ChallengeAcceptedNotification notification);

    /**
     * Called when your challenge is declined by another player
     */
    void onChallengeDeclined(ChallengeDeclinedNotification notification);
}
