/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.gameboard.model;

import com.mycompany.client.Difficulty;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author antoneos
 */
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
        char symbol = isPlayer1Turn ? 'X' : 'O';

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (difficulty == Difficulty.EASY) {
                    easyGame(symbol);
                } else if (difficulty == Difficulty.MEDIUM) {
                    medGame(symbol);
                }
                Board.WinInfo win = board.checkWin();
                if (win != null) {
                    if (win.winner == 'X') {
                        p1Wins++;
                    } else {
                        p2Wins++;
                    }
                    notifyScoreUpdate();

                    if (listener != null) {
                        listener.onGameEnd(win);
                        listener.onScoreUpdate(p1Wins, p2Wins, draws);
                        listener.onTurnChange(isPlayer1Turn);


                    }
                }
                 isPlayer1Turn = !isPlayer1Turn;

            }

        }, 1000);

    }

    void easyGame(char symbol) {
        int randomRow = 0;
        int randomCol = 0;
        do {
            randomRow = ThreadLocalRandom.current().nextInt(0, 3);
            randomCol = ThreadLocalRandom.current().nextInt(0, 3);
        } while (!board.makeMove(randomRow, randomCol, symbol));
        if (listener != null) {
            listener.onBoardUpdate(randomRow, randomCol, symbol);
        }

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
                        System.out.println("winnnn");
                        if (listener != null) {
                            listener.onBoardUpdate(i, j, symbol);
                        }
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
        char s =' ';
          if (symbol == 'X') {
            s = 'O';
        } else {
            s = 'X';
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isValidMove(i, j)) {
                    board.board[i][j] = 'X';
                    Board.WinInfo win = board.checkWin();

                    if (win != null) {
                        System.out.println("winnnn");
                        board.board[i][j] = symbol;

                        if (listener != null) {
                            listener.onBoardUpdate(i, j, symbol);
                        }
                        return true;
                    } else {

                        board.board[i][j] = ' ';
                    }

                }
            }
        }

        return false;
    }

}
