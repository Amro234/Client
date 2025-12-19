/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client;

/**
 *
 * @author Mohamed_Ali
 */

import gameLobby.PlayerStatus;
import javafx.beans.property.*;

public class Player {

    private String playerName;
    private PlayerStatus status;
    private String avatar;

    public Player(String playerName, PlayerStatus status, String avatar) {
        this.playerName = playerName;
        this.status = status;
        this.avatar = avatar;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public String getAvatar() {
        return avatar;
    }
}
