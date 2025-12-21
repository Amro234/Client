package com.mycompany.client.gameboard.model;

import com.mycompany.client.Difficulty;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class SinglePlayerSession extends GameSession {

    Difficulty difficulty;

    public SinglePlayerSession(SessionListener listener, String p1Name, String p2Name, Difficulty difficulty) {
        super(listener, p1Name, p2Name);
        this.difficulty = difficulty;
    }

    @Override
    public void handleCellClick(int row, int col) {
        char symbol = isPlayer1Turn ? 'X' : 'O';
        if (isPlayer1Turn) {
            processMove(row, col, symbol);
        }
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
        else if(difficulty==Difficulty.MEDIUM)medGame(symbol);
        else 
            hardGame(symbol);
        Board.WinInfo win = board.checkWin();
        if (win != null) {
            if (win.winner == 'X') p1Wins++;
            else p2Wins++;
            notifyScoreUpdate();
            if (listener != null) listener.onGameEnd(win);
        } else if (board.isFull()) {
            draws++;
            notifyScoreUpdate();
            if (listener != null) listener.onGameEnd(null);
        }
        isPlayer1Turn = !isPlayer1Turn;
        if (listener != null) listener.onTurnChange(isPlayer1Turn);
    }

    void easyGame(char symbol) {
        int randomRow, randomCol;
        do {
            randomRow = ThreadLocalRandom.current().nextInt(0, 3);
            randomCol = ThreadLocalRandom.current().nextInt(0, 3);
        } while (!board.isValidMove(randomRow, randomCol));
        board.makeMove(randomRow, randomCol, symbol);
        if (listener != null) listener.onBoardUpdate(randomRow, randomCol, symbol);
    }

    void medGame(char symbol) {
        if (!checkWinMed(symbol)) {
            if (!checkLosedMed(symbol)) {
                easyGame(symbol);
            }
        }
    }

    boolean checkWinMed(char symbol) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = symbol;
                    Board.WinInfo win = board.checkWin();
                    if (win != null) {
                        if (listener != null) listener.onBoardUpdate(i, j, symbol);
                        return true;
                    } else {
                        board.board[i][j] = ' ';
                    }
                }
            }
        }
        return false;
    }

    boolean checkLosedMed(char symbol) {
        char s = (symbol == 'X') ? 'O' : 'X';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = s;
                    Board.WinInfo win = board.checkWin();
                    if (win != null) {
                        board.board[i][j] = symbol;
                        if (listener != null) listener.onBoardUpdate(i, j, symbol);
                        return true;
                    } else {
                        board.board[i][j] = ' ';
                    }
                }
            }
        }
        return false;
    }
    void hardGame(char symbol) {
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

    board.makeMove(moveRow, moveCol, symbol);
    if (listener != null) listener.onBoardUpdate(moveRow, moveCol, symbol);
}

// الدالة الرئيسية للـ Minimax
int minimax(char[][] currentBoard, boolean isAI, char aiSymbol) {
    Board.WinInfo win = board.checkWin();
    if (win != null) {
        if (win.winner == aiSymbol) return 10;
        else return -10;
    }
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

// دالة مساعدة لتحديد إذا اللوحة مليانة
boolean isBoardFull(char[][] b) {
    for (int i = 0; i < 3; i++)
        for (int j = 0; j < 3; j++)
            if (b[i][j] == ' ') return false;
    return true;
}

}
