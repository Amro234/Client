/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gameLobby;

/**
 *
 * @author Mohamed_Ali
 */


public enum PlayerStatus {

    READY("READY", "#10B981"),
    IN_GAME("IN GAME", "#F59E0B"),
    AWAY("AWAY", "#94A3B8");

    private final String displayName;
    private final String color;

    PlayerStatus(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}

