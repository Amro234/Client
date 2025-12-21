package com.mycompany.client.gameboard.model;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public abstract class GameSession {
    protected Board board;
    protected boolean isPlayer1Turn;
    protected SessionListener listener;
    protected String player1Name;
    protected String player2Name;

    // Scores
    protected int p1Wins = 0;
    protected int p2Wins = 0;
    protected int draws = 0;
    Timer timer = new Timer();


    public interface SessionListener {
        void onBoardUpdate(int row, int col, char symbol);

        void onGameEnd(Board.WinInfo winInfo);

        void onTurnChange(boolean isPlayer1Turn);

        void onScoreUpdate(int p1, int p2, int draws);
    }

    public GameSession(SessionListener listener, String p1Name, String p2Name) {
        this.listener = listener;
        this.player1Name = p1Name;
        this.player2Name = p2Name;
        this.board = new Board();
        this.isPlayer1Turn = true;
    }

    public abstract void handleCellClick(int row, int col);

    public void resetGame() {
        board.reset();
        isPlayer1Turn = true;
          if (listener != null) {
            listener.onTurnChange(true);
        }
    }

    protected void processMove(int row, int col, char symbol) {
        if (board.makeMove(row, col, symbol)) {
            if (listener != null) {
                listener.onBoardUpdate(row, col, symbol);
            }

            Board.WinInfo win = board.checkWin();
            if (win != null) {
                if (win.winner == 'X')
                    p1Wins++;
                else
                    p2Wins++;
                notifyScoreUpdate();
                if (listener != null)
                    listener.onGameEnd(win);
            } else if (board.isFull()) {
                draws++;
                notifyScoreUpdate();
                if (listener != null)
                    listener.onGameEnd(null); // Null winInfo means draw
            } else {
                isPlayer1Turn = !isPlayer1Turn;
                if (listener != null)
                    listener.onTurnChange(isPlayer1Turn);
                onTurnChanged(row,col); // Hook for subclasses (AI)
            }
        }
    }

    protected void onTurnChanged(int row,int col) {
 
    }
             

    protected void notifyScoreUpdate() {
        if (listener != null) {
            listener.onScoreUpdate(p1Wins, p2Wins, draws);
        }
    }

    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
}
