package com.mycompany.client.gameboard.model;


public abstract class OnlineSession extends GameSession {

    protected String serverAddress;
    protected int serverPort;
    protected String sessionId;
    protected boolean isConnected = false;

    public OnlineSession(SessionListener listener, String localPlayerName, String opponentName,
            String serverAddress, int serverPort) {
        super(listener, localPlayerName, opponentName);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public abstract void connect();

    protected abstract void sendMoveToServer(int row, int col);
    
    public abstract void onMoveReceivedFromServer(int row, int col, char symbol);
    
    public abstract void onGameStateUpdate(String gameStateJson);

    public abstract void disconnect();

    protected abstract void onConnectionLost();
}
