package com.mycompany.client.gameboard.model;

import com.mycompany.client.Difficulty;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;

public class SinglePlayerSession extends GameSession {

    Difficulty difficulty;

    public SinglePlayerSession(SessionListener listener, String p1Name, String p2Name, Difficulty difficulty) {
        super(listener, p1Name, p2Name);
        this.difficulty = difficulty;
    }

    @Override
    public void handleCellClick(int row, int col) {
        char symbol = isPlayer1Turn ? 'X' : 'O';
        if (isPlayer1Turn && board.isValidMove(row, col)) processMove(row, col, symbol);
    }

    @Override
    protected void onTurnChanged(int row, int col) {
        char computerSymbol = isPlayer1Turn ? 'X' : 'O';
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processMoveForAI(computerSymbol);
            }
        }, 1000);
    }

    private void processMoveForAI(char symbol) {
        if (difficulty == Difficulty.EASY) easyGame(symbol);
        else if (difficulty == Difficulty.MEDIUM) medGame(symbol);
        else hardGame(symbol);

        Platform.runLater(() -> {
            Board.WinInfo win = board.checkWin();
            if (win != null) {
                if (win.winner == 'X') p1Wins++;
                else p2Wins++;
                notifyScoreUpdate();
                if (listener != null) listener.onGameEnd(win);
            } else if (isBoardFull(board.board)) {
                draws++;
                notifyScoreUpdate();
                if (listener != null) listener.onGameEnd(null);
            }
            isPlayer1Turn = !isPlayer1Turn;
            if (listener != null) listener.onTurnChange(isPlayer1Turn);
        });
    }

    void easyGame(char symbol) {
        int randomRow = -1, randomCol = -1;
        boolean found = false;
        for (int i = 0; i < 3 && !found; i++) {
            for (int j = 0; j < 3 && !found; j++) {
                if (board.isValidMove(i, j)) {
                    randomRow = i;
                    randomCol = j;
                    found = true;
                }
            }
        }
        if (found) {
            board.makeMove(randomRow, randomCol, symbol);
            final int r = randomRow, c = randomCol;
            Platform.runLater(() -> {
                if (listener != null) listener.onBoardUpdate(r, c, symbol);
            });
        }
    }

    void medGame(char symbol) {
        if (!checkWinMed(symbol)) {
            if (!checkLosedMed(symbol)) easyGame(symbol);
        }
    }

    boolean checkWinMed(char symbol) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = symbol;
                    Board.WinInfo win = board.checkWin();
                    if (win != null) {
                        final int r = i, c = j;
                        Platform.runLater(() -> {
                            if (listener != null) listener.onBoardUpdate(r, c, symbol);
                        });
                        return true;
                    } else board.board[i][j] = ' ';
                }
            }
        }
        return false;
    }

    boolean checkLosedMed(char symbol) {
        char opponent = (symbol == 'X') ? 'O' : 'X';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = opponent;
                    Board.WinInfo win = board.checkWin();
                    if (win != null) {
                        board.board[i][j] = symbol;
                        final int r = i, c = j;
                        Platform.runLater(() -> {
                            if (listener != null) listener.onBoardUpdate(r, c, symbol);
                        });
                        return true;
                    } else board.board[i][j] = ' ';
                }
            }
        }
        return false;
    }

    void hardGame(char symbol) {
        if (isBoardFull(board.board)) return;
        int bestScore = Integer.MIN_VALUE;
        int moveRow = -1, moveCol = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
            }
        }
        if (moveRow != -1 && moveCol != -1) {
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
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (currentBoard[i][j] == ' ') {
                        currentBoard[i][j] = aiSymbol;
                        int score = minimax(currentBoard, false, aiSymbol);
                        currentBoard[i][j] = ' ';
                        maxScore = Math.max(score, maxScore);
                    }
                }
            }
            return maxScore;
        } else {
            char opponent = (aiSymbol == 'X') ? 'O' : 'X';
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (currentBoard[i][j] == ' ') {
                        currentBoard[i][j] = opponent;
                        int score = minimax(currentBoard, true, aiSymbol);
                        currentBoard[i][j] = ' ';
                        minScore = Math.min(score, minScore);
                    }
                }
            }
            return minScore;
        }
    }

    boolean isBoardFull(char[][] b) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (b[i][j] == ' ') return false;
        return true;
    }
}
