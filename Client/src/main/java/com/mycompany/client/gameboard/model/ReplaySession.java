package com.mycompany.client.gameboard.model;

import java.util.List;


public abstract class ReplaySession extends GameSession {

    protected List<Move> recordedMoves;
    protected int currentMoveIndex = 0;
    protected boolean isPlaying = false;
    protected boolean isPaused = false;
    protected double playbackSpeed = 1.0; 

    public static class Move {
        public final int row;
        public final int col;
        public final char symbol;
        public final long timestamp; 
        public final String playerName;

        public Move(int row, int col, char symbol, long timestamp, String playerName) {
            this.row = row;
            this.col = col;
            this.symbol = symbol;
            this.timestamp = timestamp;
            this.playerName = playerName;
        }
    }

    public ReplaySession(SessionListener listener, String p1Name, String p2Name) {
        super(listener, p1Name, p2Name);
    }
    public abstract void loadRecording(String recordingJson);
    
    public abstract void loadRecordingFromFile(String filePath);

    public abstract void play();

    public abstract void pause();

    public abstract void resume();

    public abstract void stop();

    public abstract void stepForward();

    public abstract void stepBackward();

    public abstract void setPlaybackSpeed(double speed);

    public abstract void jumpToMove(int moveIndex);

    public abstract int getTotalMoves();

    public abstract int getCurrentMoveIndex();

    public abstract boolean isPlaying();
}
