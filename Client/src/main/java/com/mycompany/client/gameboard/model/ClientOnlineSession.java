package com.mycompany.client.gameboard.model;

import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.core.server.ServerConnection.ServerMessageListener;
import javafx.application.Platform;
import org.json.JSONObject;

import java.io.IOException;

import com.mycompany.client.core.session.UserSession;

public class ClientOnlineSession extends OnlineSession implements ServerMessageListener {

    private final String mySymbol;

    public ClientOnlineSession(SessionListener listener, String localPlayerName, String opponentName, String mySymbol) {
        super(listener, localPlayerName, opponentName, ServerConnection.getServerHost(), 5000);
        this.mySymbol = mySymbol;

        // Register as listener
        ServerConnection.setMessageListener(this);

        // Initial state
        this.isPlayer1Turn = true; // X always starts
    }

    public String getMySymbol() {
        return mySymbol;
    }

    @Override
    public void connect() {
        // Already connected
    }

    @Override
    public void handleCellClick(int row, int col) {
        boolean isMyTurn = (mySymbol.equals("X") && isPlayer1Turn) || (mySymbol.equals("O") && !isPlayer1Turn);
        if (!isMyTurn) {
            System.out.println("Not my turn!");
            return;
        }

        try {
            sendMoveToServer(row, col);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendMoveToServer(int row, int col) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "GAME_MOVE");
            json.put("row", row);
            json.put("col", col);

            // Use sendMessage (async) to avoid blocking the input stream reading
            // which is owned by the ListenerThread.
            ServerConnection.sendMessage(json.toString());

        } catch (IOException e) {
            e.printStackTrace();
            onConnectionLost();
        }
    }

    @Override
    public void onMoveReceivedFromServer(int row, int col, char symbol) {
        Platform.runLater(() -> {
            processMove(row, col, symbol);
        });
    }

    @Override
    public void onGameStateUpdate(String gameStateJson) {
        // Implement complex updates if needed
    }

    // --- Disconnection & Surrender Logic ---

    public void leaveGame() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "LEAVE_GAME");
            ServerConnection.sendMessage(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        // Called when Back is pressed or window closed
        leaveGame();
        // Remove listener?
        // ServerConnection.removeMessageListener(this); // Assuming this is desirable?
    }

    @Override
    public void onConnectionLost() {
        Platform.runLater(() -> {
            System.err.println("Connection Lost!");
            if (listener != null) {
                listener.onServerDisconnected();
            }
        });
    }

    // --- Rematch Functionality ---
    public void requestRematch() {
        sendRematchAction("REMATCH_REQUEST");
    }

    public void acceptRematch() {
        sendRematchAction("REMATCH_ACCEPT");
    }

    public void declineRematch() {
        sendRematchAction("REMATCH_DECLINE");
    }

    private void sendRematchAction(String type) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", type);
            // Async send
            ServerConnection.sendMessage(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ServerMessageListener implementation
    @Override
    public void onMessageReceived(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.optString("type", "UNKNOWN");

            if ("GAME_MOVE".equals(type)) {
                int row = json.getInt("row");
                int col = json.getInt("col");
                String symbolStr = json.getString("symbol");
                char symbol = symbolStr.charAt(0);

                onMoveReceivedFromServer(row, col, symbol);

            } else if ("GAME_OVER".equals(type)) {
                String status = json.getString("status");

                // Update session stats if provided
                if (json.has("sessionP1Wins")) {
                    this.p1Wins = json.getInt("sessionP1Wins");
                    this.p2Wins = json.getInt("sessionP2Wins");
                    this.draws = json.getInt("sessionDraws");
                }

                // Update User Score in Session
                if (json.has("p1Score") && json.has("p2Score")) {
                    int p1Score = json.getInt("p1Score");
                    int p2Score = json.getInt("p2Score");

                    int newScore = "X".equals(mySymbol) ? p1Score : p2Score;
                    UserSession.getInstance().getCurrentUser().setScore(newScore);
                }

                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onScoreUpdate(p1Wins, p2Wins, draws);
                    }
                    stopSession();

                    if ("OPPONENT_DISCONNECTED".equals(status) || "OPPONENT_LEFT".equals(status)) {
                        // Win by default
                        // Trigger special UI
                        if (listener instanceof com.mycompany.client.gameboard.controller.GameBoardController) {
                            ((com.mycompany.client.gameboard.controller.GameBoardController) listener)
                                    .onOpponentLeft(status);
                        }
                    }
                });
            } else if ("REMATCH_REQUESTED".equals(type)) {
                Platform.runLater(() -> {
                    if (listener != null)
                        listener.onRematchRequested();
                });
            } else if ("REMATCH_DECLINED".equals(type)) {
                Platform.runLater(() -> {
                    if (listener != null)
                        listener.onRematchDeclined();
                });
            } else if ("GAME_STARTED".equals(type)) {
                Platform.runLater(() -> {
                    resetGame();
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
