package com.mycompany.client.gameboard.model;

public class Board {
    private char[][] board;
    private int movesCount;

    public Board() {
        board = new char[3][3];
        reset();
    }

    // Copy constructor for AI
    public Board(Board other) {
        this.board = new char[3][3]; 
        for (int i = 0; i < 3; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, 3);
        }
        this.movesCount = other.movesCount;
    }

    public void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        movesCount = 0;
    }

    public boolean makeMove(int row, int col, char symbol) {
        if (isValidMove(row, col)) {
            board[row][col] = symbol;
            movesCount++;
            return true;
        }
        return false;
    }

    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ';
    }

    public void clearCell(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] != ' ') {
            board[row][col] = ' ';
            movesCount--;
        }
    }

    public char getCell(int row, int col) {
        return board[row][col];
    }

    public boolean isFull() {
        return movesCount >= 9;
    }

    public class WinInfo {
        public WinType type;
        public int index;
        public char winner;

        public WinInfo(WinType type, int index, char winner) {
            this.type = type;
            this.index = index;
            this.winner = winner;
        }
    }

    public enum WinType {
        HORIZONTAL, VERTICAL, DIAGONAL_MAIN, DIAGONAL_ANTI, NONE
    }

    public WinInfo checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return new WinInfo(WinType.HORIZONTAL, i, board[i][0]);
            }
        }

        for (int j = 0; j < 3; j++) {
            if (board[0][j] != ' ' && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return new WinInfo(WinType.VERTICAL, j, board[0][j]);
            }
        }

        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return new WinInfo(WinType.DIAGONAL_MAIN, -1, board[0][0]);
        }

        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return new WinInfo(WinType.DIAGONAL_ANTI, -1, board[0][2]);
        }

        return null;
    }
}
