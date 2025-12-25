package com.mycompany.client.gameLobby.networking;

import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.gameLobby.networking.exception.GameLobbyException;
import com.mycompany.client.gameLobby.networking.model.challenge.AcceptChallengeResponse;
import com.mycompany.client.gameLobby.networking.model.challenge.Challenge;
import com.mycompany.client.gameLobby.networking.model.challenge.ChallengeResponse;
import com.mycompany.client.gameLobby.networking.model.user.AvailablePlayersResponse;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUser;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUsersResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameLobbyClient {

    private static final String REQUEST_GET_ONLINE_USERS = "GET_ONLINE_USERS";
    private static final String REQUEST_GET_AVAILABLE_PLAYERS = "GET_AVAILABLE_PLAYERS";
    private static final String REQUEST_SEND_CHALLENGE = "SEND_CHALLENGE";
    private static final String REQUEST_ACCEPT_CHALLENGE = "ACCEPT_CHALLENGE";
    private static final String REQUEST_DECLINE_CHALLENGE = "DECLINE_CHALLENGE";

    /**
     * Get all online users
     */
    public static OnlineUsersResponse getOnlineUsers() throws GameLobbyException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_GET_ONLINE_USERS);

            String responseStr = ServerConnection.sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                JSONArray usersArray = response.getJSONArray("users");
                List<OnlineUser> users = new ArrayList<>();

                for (int i = 0; i < usersArray.length(); i++) {
                    users.add(OnlineUser.fromJson(usersArray.getJSONObject(i)));
                }

                int count = response.getInt("count");
                System.out.println("Retrieved " + count + " online users");
                return new OnlineUsersResponse(users, count);
            } else {
                String errorMessage = response.optString("message", "Failed to get online users");
                throw new GameLobbyException(errorMessage);
            }

        } catch (IOException e) {
            System.err.println("Network error while getting online users: " + e.getMessage());
            throw new GameLobbyException("Unable to connect to server. Please check your connection.");
        } catch (GameLobbyException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error getting online users: " + e.getMessage());
            throw new GameLobbyException("Failed to get online users: " + e.getMessage());
        }
    }

    /**
     * Get available players (not in game)
     */
    public static AvailablePlayersResponse getAvailablePlayers() throws GameLobbyException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_GET_AVAILABLE_PLAYERS);

            String responseStr = ServerConnection.sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            if (response.getBoolean("success")) {
                JSONArray playersArray = response.getJSONArray("players");
                List<OnlineUser> players = new ArrayList<>();

                for (int i = 0; i < playersArray.length(); i++) {
                    players.add(OnlineUser.fromJson(playersArray.getJSONObject(i)));
                }

                System.out.println("Retrieved " + players.size() + " available players");
                return new AvailablePlayersResponse(players);
            } else {
                String errorMessage = response.optString("message", "Failed to get available players");
                throw new GameLobbyException(errorMessage);
            }

        } catch (IOException e) {
            System.err.println("Network error while getting available players: " + e.getMessage());
            throw new GameLobbyException("Unable to connect to server. Please check your connection.");
        } catch (GameLobbyException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error getting available players: " + e.getMessage());
            throw new GameLobbyException("Failed to get available players: " + e.getMessage());
        }
    }

    /**
     * Send a challenge to another player
     */
    public static ChallengeResponse sendChallenge(int targetUserId) throws GameLobbyException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_SEND_CHALLENGE);
            request.put("targetUserId", targetUserId);

            String responseStr = ServerConnection.sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                System.out.println("Challenge sent successfully: " + message);
            } else {
                System.out.println("Failed to send challenge: " + message);
            }

            return new ChallengeResponse(success, message);

        } catch (IOException e) {
            System.err.println("Network error while sending challenge: " + e.getMessage());
            throw new GameLobbyException("Unable to connect to server. Please check your connection.");
        } catch (Exception e) {
            System.err.println("Error sending challenge: " + e.getMessage());
            throw new GameLobbyException("Failed to send challenge: " + e.getMessage());
        }
    }

    /**
     * Accept a pending challenge
     */
    public static AcceptChallengeResponse acceptChallenge() throws GameLobbyException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_ACCEPT_CHALLENGE);

            String responseStr = ServerConnection.sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            Challenge opponent = null;
            JSONObject gameDetails = null;
            if (success) {
                if (response.has("opponent")) {
                    opponent = Challenge.fromJson(response.getJSONObject("opponent"));
                }
                if (response.has("gameDetails")) {
                    gameDetails = response.getJSONObject("gameDetails");
                }
                System.out.println("Challenge accepted.");
            } else {
                System.out.println("Failed to accept challenge: " + message);
            }

            return new AcceptChallengeResponse(success, message, opponent, gameDetails);

        } catch (IOException e) {
            System.err.println("Network error while accepting challenge: " + e.getMessage());
            throw new GameLobbyException("Unable to connect to server. Please check your connection.");
        } catch (Exception e) {
            System.err.println("Error accepting challenge: " + e.getMessage());
            throw new GameLobbyException("Failed to accept challenge: " + e.getMessage());
        }
    }

    /**
     * Decline a pending challenge
     */
    public static ChallengeResponse declineChallenge() throws GameLobbyException {
        try {
            JSONObject request = new JSONObject();
            request.put("type", REQUEST_DECLINE_CHALLENGE);

            String responseStr = ServerConnection.sendRequest(request.toString());
            JSONObject response = new JSONObject(responseStr);

            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                System.out.println("Challenge declined successfully: " + message);
            } else {
                System.out.println("Failed to decline challenge: " + message);
            }

            return new ChallengeResponse(success, message);

        } catch (IOException e) {
            System.err.println("Network error while declining challenge: " + e.getMessage());
            throw new GameLobbyException("Unable to connect to server. Please check your connection.");
        } catch (Exception e) {
            System.err.println("Error declining challenge: " + e.getMessage());
            throw new GameLobbyException("Failed to decline challenge: " + e.getMessage());
        }
    }
}
