package com.mycompany.client.gameboard.model;

import java.util.Timer;

public abstract class GameSession {

    public Board board;
    protected boolean isPlayer1Turn;
    protected SessionListener listener;
    protected String player1Name;
    protected String player2Name;

    // Scores
    protected int p1Wins = 0;
    protected int p2Wins = 0;
    protected int draws = 0;

    // 🔥 Timer واحد لكل Session
    protected Timer timer = new Timer(true); // daemon thread

    public interface SessionListener {
        void onBoardUpdate(int row, int col, char symbol);

        void onGameEnd(Board.WinInfo winInfo);

        void onTurnChange(boolean isPlayer1Turn);

        void onScoreUpdate(int p1, int p2, int draws);

        void onReplayFinished();

        void onSessionReset();

        // Online Rematch
        default void onRematchRequested() {
        }

        default void onRematchDeclined() {
        }

        default void onServerDisconnected() {
        }
    }

    public GameSession(SessionListener listener, String p1Name, String p2Name) {
        this.listener = listener;
        this.player1Name = p1Name;
        this.player2Name = p2Name;
        this.board = new Board();
        this.isPlayer1Turn = true;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public abstract boolean handleCellClick(int row, int col);

    // 🔥 مهم جدًا
    public void stopSession() {
        System.out.println("[GameSession] stopSession() called – Timer cancelled");
        try {
            timer.cancel();
            timer.purge();
        } catch (Exception ignored) {
        }
        timer = null;
    }

    public void resetGame() {
        System.out.println("[GameSession] resetGame()");
        stopSession(); // 🔥 اقفل أي AI شغال
        timer = new Timer(true); // 🔥 Timer جديد
        board.reset();
        isPlayer1Turn = true;
        if (listener != null) {
            listener.onSessionReset();
            listener.onTurnChange(true);
        }
    }

    protected boolean processMove(int row, int col, char symbol) {
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

                stopSession(); // 🔥 مهم
                if (listener != null)
                    listener.onGameEnd(win);

            } else if (board.isFull()) {
                draws++;
                notifyScoreUpdate();

                stopSession(); // 🔥 مهم
                if (listener != null)
                    listener.onGameEnd(null);

            } else {
                isPlayer1Turn = !isPlayer1Turn;
                if (listener != null)
                    listener.onTurnChange(isPlayer1Turn);
                onTurnChanged();
            }
            return true;
        }
        return false;
    }

    protected void onTurnChanged() {

        // hook

        // Optional hook

    }

    protected void notifyScoreUpdate() {
        if (listener != null) {
            listener.onScoreUpdate(p1Wins, p2Wins, draws);
        }
    }

    public void forceSwitchTurn() {
        isPlayer1Turn = !isPlayer1Turn;

        if (listener != null)
            listener.onTurnChange(isPlayer1Turn);
        onTurnChanged(); // Hook for subclasses (AI)

    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge(); // Removes all cancelled tasks from this timer's task queue.
        }
        disconnect();
    }

    public void disconnect() {
    } // Optional hook for subclasses (e.g. online)
}
