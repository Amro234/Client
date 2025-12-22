package com.mycompany.client.gameboard.model;

import com.mycompany.client.difficulty.Difficulty;
import java.util.TimerTask;
import javafx.application.Platform;

public class SinglePlayerSession extends GameSession {

    private Difficulty difficulty;

    public SinglePlayerSession(SessionListener listener,
                               String p1Name,
                               String p2Name,
                               Difficulty difficulty) {
        super(listener, p1Name, p2Name);
        this.difficulty = difficulty;
    }

    @Override
    public void handleCellClick(int row, int col) {
        if (!isPlayer1Turn) return;

        char symbol = 'X';
        if (board.isValidMove(row, col)) {
            processMove(row, col, symbol);
        }
    }

    @Override
    protected void onTurnChanged() {

        // لو السيشن اتقفلت
        if (timer == null) {
            System.out.println("[AI] onTurnChanged skipped – session closed");
            return;
        }

        char computerSymbol = 'O';

        System.out.println("[AI] Scheduling AI move...");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (timer == null) {
                    System.out.println("[AI] Timer task cancelled – session closed");
                    return;
                }

                processMoveForAI(computerSymbol);
            }
        }, 1000);
    }

    private void processMoveForAI(char symbol) {

        if (timer == null) {
            System.out.println("[AI] processMoveForAI aborted – session closed");
            return;
        }

        System.out.println("[AI] Thinking... Difficulty = " + difficulty);

        if (difficulty == Difficulty.EASY) easyGame(symbol);
        else if (difficulty == Difficulty.MEDIUM) medGame(symbol);
        else hardGame(symbol);

        Platform.runLater(() -> {

            if (timer == null) {
                System.out.println("[AI] UI update aborted – session closed");
                return;
            }

            Board.WinInfo win = board.checkWin();

            if (win != null) {
                if (win.winner == 'X') p1Wins++;
                else p2Wins++;
                notifyScoreUpdate();

                System.out.println("[AI] Game End detected – stopping session");
                stopSession();

                if (listener != null) listener.onGameEnd(win);
                return;
            }

            if (isBoardFull(board.board)) {
                draws++;
                notifyScoreUpdate();

                System.out.println("[AI] Draw detected – stopping session");
                stopSession();

                if (listener != null) listener.onGameEnd(null);
                return;
            }

            isPlayer1Turn = true;
            if (listener != null) listener.onTurnChange(true);
        });
    }

    /* ================= AI LOGIC (كما هو) ================= */

    void easyGame(char symbol) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board.isValidMove(i, j)) {
                    board.makeMove(i, j, symbol);
                    final int r = i, c = j;
                    Platform.runLater(() -> {
                        if (listener != null) listener.onBoardUpdate(r, c, symbol);
                    });
                    return;
                }
    }

    void medGame(char symbol) {
        if (!checkWinMed(symbol)) {
            if (!checkLosedMed(symbol)) easyGame(symbol);
        }
    }

    boolean checkWinMed(char symbol) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = symbol;
                    if (board.checkWin() != null) {
                        board.board[i][j] = symbol;
                        final int r = i, c = j;
                        Platform.runLater(() -> {
                            if (listener != null) listener.onBoardUpdate(r, c, symbol);
                        });
                        return true;
                    }
                    board.board[i][j] = ' ';
                }
        return false;
    }

    boolean checkLosedMed(char symbol) {
        char opponent = (symbol == 'X') ? 'O' : 'X';
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = opponent;
                    if (board.checkWin() != null) {
                        board.board[i][j] = symbol;
                        final int r = i, c = j;
                        Platform.runLater(() -> {
                            if (listener != null) listener.onBoardUpdate(r, c, symbol);
                        });
                        return true;
                    }
                    board.board[i][j] = ' ';
                }
        return false;
    }

    void hardGame(char symbol) {
        if (isBoardFull(board.board)) return;

        int bestScore = Integer.MIN_VALUE;
        int moveRow = -1, moveCol = -1;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = symbol;
                    int score = minimax(board.board, false, symbol);
                    board.board[i][j] = ' ';
                    if (score > bestScore) {
                        bestScore = score;
                        moveRow = i;
                        moveCol = j;
                    }
                }

        if (moveRow != -1) {
            board.makeMove(moveRow, moveCol, symbol);
            final int r = moveRow, c = moveCol;
            Platform.runLater(() -> {
                if (listener != null) listener.onBoardUpdate(r, c, symbol);
            });
        }
    }

    int minimax(char[][] currentBoard, boolean isAI, char aiSymbol) {
        Board.WinInfo win = board.checkWin();
        if (win != null) return (win.winner == aiSymbol) ? 10 : -10;
        if (isBoardFull(currentBoard)) return 0;

        if (isAI) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (currentBoard[i][j] == ' ') {
                        currentBoard[i][j] = aiSymbol;
                        max = Math.max(max, minimax(currentBoard, false, aiSymbol));
                        currentBoard[i][j] = ' ';
                    }
            return max;
        } else {
            char opp = aiSymbol == 'X' ? 'O' : 'X';
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (currentBoard[i][j] == ' ') {
                        currentBoard[i][j] = opp;
                        min = Math.min(min, minimax(currentBoard, true, aiSymbol));
                        currentBoard[i][j] = ' ';
                    }
            return min;
        }
    }

    boolean isBoardFull(char[][] b) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (b[i][j] == ' ') return false;
        return true;
    }
}
