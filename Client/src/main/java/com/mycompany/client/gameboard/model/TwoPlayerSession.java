package com.mycompany.client.gameboard.model;

public class TwoPlayerSession extends GameSession {

    public TwoPlayerSession(SessionListener listener, String p1Name, String p2Name) {
        super(listener, p1Name, p2Name);
    }

    @Override
    public boolean handleCellClick(int row, int col) {

        char symbol = isPlayer1Turn ? 'X' : 'O';
        return processMove(row, col, symbol);
    }
}
